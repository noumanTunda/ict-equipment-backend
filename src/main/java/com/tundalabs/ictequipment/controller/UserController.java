package com.tundalabs.ictequipment.controller;

import com.tundalabs.ictequipment.dto.ApiResponse;
import com.tundalabs.ictequipment.dto.SetKeyphraseRequestDto;
import com.tundalabs.ictequipment.dto.UpdateKeyphraseRequestDto;
import com.tundalabs.ictequipment.dto.UserSearchResponseDto;
import com.tundalabs.ictequipment.entity.User;
import com.tundalabs.ictequipment.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    @PostMapping("/keyphrase")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Set keyphrase for transaction signing", description = "Sets a keyphrase for the authenticated user to sign transactions. Keyphrase is hashed and stored securely.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Keyphrase set successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request or user already has a keyphrase"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Void>> setKeyphrase(
            @Valid @RequestBody SetKeyphraseRequestDto request,
            Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        log.info("Setting keyphrase for user ID: {}", userId);
        userService.setKeyphrase(userId, request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Keyphrase set successfully", null));
    }

    @PutMapping("/keyphrase")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update keyphrase for transaction signing", description = "Updates the existing keyphrase for the authenticated user. Requires current keyphrase for verification.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Keyphrase updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request or incorrect current keyphrase"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Void>> updateKeyphrase(
            @Valid @RequestBody UpdateKeyphraseRequestDto request,
            Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        log.info("Updating keyphrase for user ID: {}", userId);
        userService.updateKeyphrase(userId, request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Keyphrase updated successfully", null));
    }

    @GetMapping("/keyphrase/status")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Check keyphrase status", description = "Returns true if the authenticated user has a keyphrase configured.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Keyphrase status retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Boolean>> hasKeyphrase(Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        boolean exists = userService.hasKeyphrase(userId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Keyphrase status retrieved", exists));
    }

    private Long getAuthenticatedUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof User) {
            return ((User) principal).getId();
        }
        
        if (principal instanceof String) {
            throw new IllegalStateException("Unexpected principal type: String. Expected User object.");
        }
        
        throw new IllegalStateException("Unexpected principal type: " + principal.getClass().getName());
    }
}
