package com.tundalabs.ictequipment.controller;

import com.tundalabs.ictequipment.dto.ApiResponse;
import com.tundalabs.ictequipment.dto.EquipmentRequestDto;
import com.tundalabs.ictequipment.dto.EquipmentResponseDto;
import com.tundalabs.ictequipment.service.EquipmentService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/equipment")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Equipment Management", description = "APIs for managing ICT equipment (Admin and ICT Officer only)")
@SecurityRequirement(name = "bearerAuth")
public class EquipmentController {

    private final EquipmentService equipmentService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ICT_OFFICER')")
    @Operation(summary = "Create new equipment", description = "Creates a new equipment record. Requires ADMIN or ICT_OFFICER role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Equipment created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data or duplicate asset/serial number"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<EquipmentResponseDto>> createEquipment(
            @Valid @RequestBody EquipmentRequestDto request) {
        log.info("Creating new equipment with asset number: {}", request.getAssetNumber());
        EquipmentResponseDto response = equipmentService.createEquipment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Equipment created successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ICT_OFFICER')")
    @Operation(summary = "Get equipment by ID", description = "Retrieves a specific equipment by its ID. Requires ADMIN or ICT_OFFICER role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Equipment retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Equipment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<EquipmentResponseDto>> getEquipmentById(
            @Parameter(description = "Equipment ID") @PathVariable Long id) {
        log.info("Fetching equipment by ID: {}", id);
        EquipmentResponseDto response = equipmentService.getEquipmentById(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Equipment retrieved successfully", response));
    }

    @GetMapping("/asset/{assetNumber}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ICT_OFFICER')")
    @Operation(summary = "Get equipment by asset number", description = "Retrieves a specific equipment by its asset number. Requires ADMIN or ICT_OFFICER role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Equipment retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Equipment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<EquipmentResponseDto>> getEquipmentByAssetNumber(
            @Parameter(description = "Asset number") @PathVariable String assetNumber) {
        log.info("Fetching equipment by asset number: {}", assetNumber);
        EquipmentResponseDto response = equipmentService.getEquipmentByAssetNumber(assetNumber);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Equipment retrieved successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ICT_OFFICER')")
    @Operation(summary = "Get all equipment (paginated)", description = "Retrieves a paginated list of all equipment. Requires ADMIN or ICT_OFFICER role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Equipment retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Page<EquipmentResponseDto>>> getEquipment(Pageable pageable) {
        log.info("Fetching equipment with pagination");
        Page<EquipmentResponseDto> response = equipmentService.getEquipment(pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Equipment retrieved successfully", response));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ICT_OFFICER')")
    @Operation(summary = "Get all equipment (non-paginated)", description = "Retrieves a list of all equipment without pagination. Requires ADMIN or ICT_OFFICER role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Equipment retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<java.util.List<EquipmentResponseDto>>> getAllEquipment() {
        log.info("Fetching all equipment");
        java.util.List<EquipmentResponseDto> response = equipmentService.getAllEquipment();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Equipment retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ICT_OFFICER')")
    @Operation(summary = "Update equipment", description = "Updates an existing equipment record. Requires ADMIN or ICT_OFFICER role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Equipment updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data or duplicate asset/serial number"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Equipment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<EquipmentResponseDto>> updateEquipment(
            @Parameter(description = "Equipment ID") @PathVariable Long id,
            @Valid @RequestBody EquipmentRequestDto request) {
        log.info("Updating equipment with ID: {}", id);
        EquipmentResponseDto response = equipmentService.updateEquipment(id, request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Equipment updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ICT_OFFICER')")
    @Operation(summary = "Delete equipment", description = "Deletes an equipment record. Cannot delete equipment that is currently issued. Requires ADMIN or ICT_OFFICER role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Equipment deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Equipment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot delete equipment that is currently issued"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Void>> deleteEquipment(
            @Parameter(description = "Equipment ID") @PathVariable Long id) {
        log.info("Deleting equipment with ID: {}", id);
        equipmentService.deleteEquipment(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Equipment deleted successfully"));
    }
}
