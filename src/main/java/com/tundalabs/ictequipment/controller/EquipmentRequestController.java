package com.tundalabs.ictequipment.controller;

import com.tundalabs.ictequipment.dto.*;
import com.tundalabs.ictequipment.entity.EquipmentRequest;
import com.tundalabs.ictequipment.entity.User;
import com.tundalabs.ictequipment.service.EquipmentRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/equipment-requests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Equipment Requests", description = "APIs for staff to request equipment and ICT Officers/Admins to approve/reject")
@SecurityRequirement(name = "bearerAuth")
public class EquipmentRequestController {

    private final EquipmentRequestService equipmentRequestService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Submit equipment request", description = "Staff member submits a request to issue, return, or exchange equipment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Request submitted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data or equipment not available"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<EquipmentRequestResponseDto>> createRequest(
            @Valid @RequestBody CreateEquipmentRequestDto request,
            Authentication authentication) {
        Long staffId = getAuthenticatedUserId(authentication);
        log.info("Creating equipment request for staff ID: {}, type: {}", staffId, request.getRequestType());
        EquipmentRequestResponseDto response = equipmentRequestService.createRequest(staffId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Request submitted successfully", response));
    }

    @GetMapping("/my-requests")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my equipment requests", description = "Staff member retrieves their own equipment requests")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Requests retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Page<EquipmentRequestResponseDto>>> getMyRequests(
            Pageable pageable,
            Authentication authentication) {
        Long staffId = getAuthenticatedUserId(authentication);
        log.info("Fetching equipment requests for staff ID: {}", staffId);
        Page<EquipmentRequestResponseDto> response = equipmentRequestService.getMyRequests(staffId, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Requests retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get request by ID", description = "Retrieves a specific equipment request by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Request not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<EquipmentRequestResponseDto>> getRequestById(
            @Parameter(description = "Request ID") @PathVariable Long id) {
        log.info("Fetching equipment request by ID: {}", id);
        EquipmentRequestResponseDto response = equipmentRequestService.getRequestById(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Request retrieved successfully", response));
    }

    @GetMapping("/code/{requestCode}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get request by code", description = "Retrieves a specific equipment request by request code")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Request not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<EquipmentRequestResponseDto>> getRequestByCode(
            @Parameter(description = "Request code") @PathVariable String requestCode) {
        log.info("Fetching equipment request by code: {}", requestCode);
        EquipmentRequestResponseDto response = equipmentRequestService.getRequestByCode(requestCode);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Request retrieved successfully", response));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ICT_OFFICER', 'ROLE_ADMIN')")
    @Operation(summary = "Get all pending requests (Admin/ICT Officer)", description = "ICT Officers and Admins can view all pending equipment requests")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Requests retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Page<EquipmentRequestResponseDto>>> getAllPendingRequests(
            Pageable pageable) {
        log.info("Fetching all pending equipment requests");
        Page<EquipmentRequestResponseDto> response = equipmentRequestService.getAllRequests(
                EquipmentRequest.RequestStatus.PENDING, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Requests retrieved successfully", response));
    }

    @GetMapping("/admin/all/{status}")
    @PreAuthorize("hasAnyAuthority('ROLE_ICT_OFFICER', 'ROLE_ADMIN')")
    @Operation(summary = "Get all requests by status (Admin/ICT Officer)", description = "ICT Officers and Admins can view equipment requests by status")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Requests retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Page<EquipmentRequestResponseDto>>> getAllRequestsByStatus(
            @Parameter(description = "Request status (PENDING, APPROVED, REJECTED, COMPLETED)") 
            @PathVariable com.tundalabs.ictequipment.entity.EquipmentRequest.RequestStatus status,
            Pageable pageable) {
        log.info("Fetching equipment requests by status: {}", status);
        Page<EquipmentRequestResponseDto> response = equipmentRequestService.getAllRequests(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Requests retrieved successfully", response));
    }

    @PostMapping("/approve")
    @PreAuthorize("hasAnyAuthority('ROLE_ICT_OFFICER', 'ROLE_ADMIN')")
    @Operation(summary = "Approve equipment request", description = "ICT Officer or Admin approves a pending equipment request and creates the transaction")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request approved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data or request not in PENDING status"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Request not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<EquipmentRequestResponseDto>> approveRequest(
            @Valid @RequestBody ApproveEquipmentRequestDto request,
            Authentication authentication) {
        Long officerId = getAuthenticatedUserId(authentication);
        log.info("Approving equipment request ID: {} by officer ID: {}", request.getRequestId(), officerId);
        EquipmentRequestResponseDto response = equipmentRequestService.approveRequest(officerId, request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Request approved successfully", response));
    }

    @PostMapping("/{requestId}/reject")
    @PreAuthorize("hasAnyAuthority('ROLE_ICT_OFFICER', 'ROLE_ADMIN')")
    @Operation(summary = "Reject equipment request", description = "ICT Officer or Admin rejects a pending equipment request")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request rejected successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data or request not in PENDING status"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Request not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<EquipmentRequestResponseDto>> rejectRequest(
            @Parameter(description = "Request ID") @PathVariable Long requestId,
            @Valid @RequestBody RejectEquipmentRequestDto request,
            Authentication authentication) {
        Long officerId = getAuthenticatedUserId(authentication);
        log.info("Rejecting equipment request ID: {} by officer ID: {}", requestId, officerId);
        EquipmentRequestResponseDto response = equipmentRequestService.rejectRequest(officerId, requestId, request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Request rejected successfully", response));
    }

    private Long getAuthenticatedUserId(Authentication authentication) {
        // The authentication principal is a User object (implements UserDetails)
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof User) {
            // Principal is the User entity, return its ID
            return ((User) principal).getId();
        }
        
        // Fallback: if principal is a String (employeeId), look up the user
        if (principal instanceof String) {
            String employeeId = (String) principal;
            // Note: We'd need to inject UserRepository here, but since we're in a controller
            // and the User object should be available as principal, this is a fallback
            throw new IllegalStateException("Unexpected principal type: String. Expected User object.");
        }
        
        throw new IllegalStateException("Unexpected principal type: " + principal.getClass().getName());
    }
}
