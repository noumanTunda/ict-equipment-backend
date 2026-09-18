package com.tundalabs.ictequipment.service;

import com.tundalabs.ictequipment.dto.SetKeyphraseRequestDto;
import com.tundalabs.ictequipment.dto.UpdateKeyphraseRequestDto;
import com.tundalabs.ictequipment.dto.UserSearchResponseDto;

import java.util.List;

public interface UserService {

    List<UserSearchResponseDto> searchUsers(String query);

    void setKeyphrase(Long userId, SetKeyphraseRequestDto request);

    void updateKeyphrase(Long userId, UpdateKeyphraseRequestDto request);

    boolean hasKeyphrase(Long userId);
}
