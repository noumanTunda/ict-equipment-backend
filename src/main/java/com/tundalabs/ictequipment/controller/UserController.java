package com.tundalabs.ictequipment.controller;

import com.tundalabs.ictequipment.dto.ApiResponse;
import com.tundalabs.ictequipment.dto.UserSearchResponseDto;
import com.tundalabs.ictequipment.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for user search and management")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ICT_OFFICER')")
    @Operation(summary = "Search active users", description = "Searches for active staff members by email, full name or employee ID. Requires ADMIN or ICT_OFFICER role.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<List<UserSearchResponseDto>>> searchUsers(
            @Parameter(description = "Search query term") @RequestParam(required = false) String query) {
        log.info("Searching users with query: {}", query);
        List<UserSearchResponseDto> response = userService.searchUsers(query);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Users retrieved successfully", response));
    }
}
