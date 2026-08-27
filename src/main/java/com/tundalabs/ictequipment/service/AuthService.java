package com.tundalabs.ictequipment.service;

import com.tundalabs.ictequipment.dto.*;

public interface AuthService {
    JwtResponseDto login(LoginRequestDto loginRequest);
    JwtResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequest);
    ApiResponse<UserProfileDto> getCurrentUser();
    ApiResponse<UserProfileDto> register(RegisterRequestDto registerRequest);
    ApiResponse<Void> changePassword(ChangePasswordRequestDto resetPasswordRequest);
    ApiResponse<Void> requestPasswordReset(ResetPasswordRequestDto dto);
    ApiResponse<Void> resetPassword(ResetPasswordDto dto);
}
