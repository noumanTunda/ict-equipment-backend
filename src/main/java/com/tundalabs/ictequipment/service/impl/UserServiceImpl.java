package com.tundalabs.ictequipment.service.impl;

import com.tundalabs.ictequipment.dto.SetKeyphraseRequestDto;
import com.tundalabs.ictequipment.dto.UpdateKeyphraseRequestDto;
import com.tundalabs.ictequipment.dto.UserSearchResponseDto;
import com.tundalabs.ictequipment.entity.User;
import com.tundalabs.ictequipment.repository.UserRepository;
import com.tundalabs.ictequipment.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    @Override
    @Transactional
    public void setKeyphrase(Long userId, SetKeyphraseRequestDto request) {
        log.info("Setting keyphrase for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (user.getKeyphrase() != null) {
            throw new RuntimeException("User already has a keyphrase set. Use update endpoint to change it.");
        }

        String hashedKeyphrase = passwordEncoder.encode(request.getKeyphrase());
        user.setKeyphrase(hashedKeyphrase);
        userRepository.save(user);

        log.info("Keyphrase set successfully for user ID: {}", userId);
    }

    @Override
    @Transactional
    public void updateKeyphrase(Long userId, UpdateKeyphraseRequestDto request) {
        log.info("Updating keyphrase for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (user.getKeyphrase() == null) {
            throw new RuntimeException("User does not have a keyphrase set. Use set endpoint to create one.");
        }

        // Validate current keyphrase
        if (!passwordEncoder.matches(request.getCurrentKeyphrase(), user.getKeyphrase())) {
            throw new RuntimeException("Invalid current keyphrase");
        }

        String hashedNewKeyphrase = passwordEncoder.encode(request.getNewKeyphrase());
        user.setKeyphrase(hashedNewKeyphrase);
        userRepository.save(user);

        log.info("Keyphrase updated successfully for user ID: {}", userId);
    }
}
