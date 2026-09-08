package com.tundalabs.ictequipment.service;

import com.tundalabs.ictequipment.dto.UserSearchResponseDto;

import java.util.List;

public interface UserService {

    List<UserSearchResponseDto> searchUsers(String query);
}
