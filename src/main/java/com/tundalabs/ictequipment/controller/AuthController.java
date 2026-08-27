package com.tundalabs.ictequipment.controller;

import com.tundalabs.ictequipment.dto.*;
import com.tundalabs.ictequipment.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "APIs for user authentication and authorization")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns JWT tokens")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<JwtResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getEmployeeIdOrEmail());
        JwtResponseDto response = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Login successful", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT token", description = "Refreshes an expired JWT token using a refresh token")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    public ResponseEntity<ApiResponse<JwtResponseDto>> refreshToken(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequest) {
        log.info("Token refresh request");
        JwtResponseDto response = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Token refreshed successfully", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Retrieves the profile of the currently authenticated user")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<ApiResponse<UserProfileDto>> getCurrentUser() {
        log.info("Get current user profile request");
        ApiResponse<UserProfileDto> response = authService.getCurrentUser();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Registers a new user account")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data or user already exists")
    })
    public ResponseEntity<ApiResponse<UserProfileDto>> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        log.info("Registration attempt for employee ID: {}", registerRequest.getEmployeeId());
        ApiResponse<UserProfileDto> response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Initiates a password change for a user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password Change initiated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequestDto changePasswordRequest) {
        log.info("Password change request for email: {}", changePasswordRequest.getEmail());
        ApiResponse<Void> response = authService.changePassword(changePasswordRequest);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/forgot-password")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password reset link sent if account exists")
    })
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(@Valid @RequestBody ResetPasswordRequestDto request) {
        log.info("Password reset requested for email: {}", request.getEmail());
        ApiResponse<Void> response = authService.requestPasswordReset(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid payload or expired token")
    })
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordDto dto) {
        log.info("Executing password reset for token: {}", dto.getToken());
        ApiResponse<Void> response = authService.resetPassword(dto);
        return ResponseEntity.ok(response);
    }
}
