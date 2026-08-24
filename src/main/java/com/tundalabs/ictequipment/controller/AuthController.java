package com.tundalabs.ictequipment.controller;

import com.tundalabs.ictequipment.dto.*;
import com.tundalabs.ictequipment.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getEmployeeIdOrEmail());
        JwtResponseDto response = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtResponseDto>> refreshToken(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequest) {
        log.info("Token refresh request");
        JwtResponseDto response = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Token refreshed successfully", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileDto>> getCurrentUser() {
        log.info("Get current user profile request");
        ApiResponse<UserProfileDto> response = authService.getCurrentUser();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserProfileDto>> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        log.info("Registration attempt for employee ID: {}", registerRequest.getEmployeeId());
        ApiResponse<UserProfileDto> response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequestDto resetPasswordRequest) {
        log.info("Password reset request for email: {}", resetPasswordRequest.getEmail());
        ApiResponse<Void> response = authService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok(response);
    }
}
