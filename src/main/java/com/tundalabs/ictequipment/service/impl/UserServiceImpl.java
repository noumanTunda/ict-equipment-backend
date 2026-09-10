package com.tundalabs.ictequipment.service.impl;

import com.tundalabs.ictequipment.dto.UserSearchResponseDto;
import com.tundalabs.ictequipment.entity.User;
import com.tundalabs.ictequipment.repository.UserRepository;
import com.tundalabs.ictequipment.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserSearchResponseDto> searchUsers(String query) {
        log.info("Searching users with query: {}", query);
        
        List<User> users;
        if (query == null || query.trim().isEmpty()) {
            users = userRepository.findAll();
        } else {
            users = userRepository.searchActiveUsers(query);
        }

        return users.stream()
                .map(this::mapToSearchResponseDto)
                .collect(Collectors.toList());
    }

    private UserSearchResponseDto mapToSearchResponseDto(User user) {
        return UserSearchResponseDto.builder()
                .id(user.getId())
                .employeeId(user.getEmployeeId())
                .department(user.getDepartment().name())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }
}
