package com.tundalabs.ictequipment.service.impl;

import com.tundalabs.ictequipment.dto.*;
import com.tundalabs.ictequipment.entity.PasswordResetToken;
import com.tundalabs.ictequipment.entity.User;
import com.tundalabs.ictequipment.repository.PasswordResetTokenRepository;
import com.tundalabs.ictequipment.repository.UserRepository;
import com.tundalabs.ictequipment.security.JwtTokenProvider;
import com.tundalabs.ictequipment.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailServiceImpl emailService;

    @Override
    public JwtResponseDto login(LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmployeeIdOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        User user = userRepository.findByEmployeeId(loginRequest.getEmployeeIdOrEmail())
                .orElseGet(() -> userRepository.findByEmail(loginRequest.getEmployeeIdOrEmail())
                        .orElseThrow(() -> new RuntimeException("User not found")));

        return JwtResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(user.toUserProfileDto())
                .build();
    }

    @Override
    public JwtResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByEmployeeId(username)
                .orElseGet(() -> userRepository.findByEmail(username)
                        .orElseThrow(() -> new RuntimeException("User not found")));

        String newAccessToken = tokenProvider.generateAccessToken(username);
        String newRefreshToken = tokenProvider.generateRefreshToken(username);

        return JwtResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .user(user.toUserProfileDto())
                .build();
    }

    @Override
    public ApiResponse<UserProfileDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String username = authentication.getName();
        User user = userRepository.findByEmployeeId(username)
                .orElseGet(() -> userRepository.findByEmail(username)
                        .orElseThrow(() -> new RuntimeException("User not found")));

        return ApiResponse.success(200, "User profile retrieved successfully", user.toUserProfileDto());
    }

    @Override
    public ApiResponse<UserProfileDto> register(RegisterRequestDto registerRequest) {
        if (userRepository.existsByEmployeeId(registerRequest.getEmployeeId())) {
            throw new RuntimeException("Employee ID already exists");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User.UserRole userRole;
        try {
            userRole = User.UserRole.valueOf(registerRequest.getRole());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role. Valid roles are: ROLE_STAFF, ROLE_ICT_OFFICER, ROLE_ADMIN");
        }

        User user = User.builder()
                .employeeId(registerRequest.getEmployeeId())
                .fullName(registerRequest.getFullName())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .department(registerRequest.getDepartment())
                .role(userRole)
                .mobileNo(registerRequest.getMobileNo())
                .email(registerRequest.getEmail())
                .status(User.UserStatus.active)
                .build();

        User savedUser = userRepository.save(user);
        log.info("New user registered with employee ID: {}", savedUser.getEmployeeId());

        return ApiResponse.success(201, "User registered successfully", savedUser.toUserProfileDto());
    }

    @Override
    public ApiResponse<Void> changePassword(ChangePasswordRequestDto changePasswordRequest) {
        User user = userRepository.findByEmail(changePasswordRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + changePasswordRequest.getEmail()));

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid current password");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);

        log.info("Password successfully changed for email: {}", user.getEmail());

        return ApiResponse.success(200, "Password Changed successfully", null);
    }

    @Transactional
    public ApiResponse<Void> requestPasswordReset(ResetPasswordRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElse(null);

        if (user != null) {
            PasswordResetToken resetToken = tokenRepository.findByUser(user)
                    .orElseGet(() -> PasswordResetToken.builder()
                            .user(user)
                            .build());

            String newToken = UUID.randomUUID().toString();
            resetToken.setToken(newToken);
            resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));

            tokenRepository.save(resetToken);

            emailService.sendPasswordResetEmail(user.getEmail(), newToken);
            log.info("Password reset token generated/updated for user {}: {}", user.getEmail(), newToken);
        } else {
            log.warn("Password reset requested for non-existent email: {}", dto.getEmail());
        }

        return ApiResponse.success(200, "If an account exists with that email, a password reset link has been sent.");
    }

    @Transactional
    public ApiResponse<Void> resetPassword(ResetPasswordDto dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match.");
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired password reset token."));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken);
            throw new IllegalStateException("Password reset token has expired.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        tokenRepository.delete(resetToken);
        log.info("Password successfully updated for user: {}", user.getEmail());

        return ApiResponse.success(200, "Password has been successfully updated.");
    }

}
