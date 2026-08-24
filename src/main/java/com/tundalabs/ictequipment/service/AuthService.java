package com.tundalabs.ictequipment.service;

import com.tundalabs.ictequipment.dto.*;

public interface AuthService {
    JwtResponseDto login(LoginRequestDto loginRequest);
    JwtResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequest);
    ApiResponse<UserProfileDto> getCurrentUser();
}
