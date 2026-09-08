package com.tundalabs.ictequipment.dto;

import com.tundalabs.ictequipment.entity.Equipment;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating or updating equipment")
public class EquipmentRequestDto {
    
    @NotBlank(message = "Asset number is required")
    @Size(max = 50, message = "Asset number must not exceed 50 characters")
    @Schema(description = "Unique asset number", example = "AST-2024-001")
    private String assetNumber;

    @NotBlank(message = "Serial number is required")
    @Size(max = 100, message = "Serial number must not exceed 100 characters")
    @Schema(description = "Unique serial number", example = "SN 123456789")
    private String serialNumber;

    @NotBlank(message = "Equipment type is required")
    @Size(max = 100, message = "Equipment type must not exceed 100 characters")
    @Schema(description = "Type of equipment", example = "Laptop")
    private String equipmentType;

    @NotBlank(message = "Brand Model is required")
    @Size(max = 100, message = "Brand Model must not exceed 100 characters")
    @Schema(description = "Brand or Model of equipment", example = "HP ProBook 6470")
    private String brandModel;

    @Size(max = 255, message = "Brand Model must not exceed 255 characters")
    @Schema(description = "Supplier Details for this equipment", example = "Supplier Name")
    private String supplierDetails;

    @NotNull(message = "Status is required")
    @Schema(description = "Equipment status", example = "AVAILABLE")
    private Equipment.EquipmentStatus status;

    @NotNull(message = "Equipment must belong to a Department")
    @Schema(description = "Equipment department", example = "ICT")
    private Equipment.EquipmentDepartment department;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Schema(description = "Equipment description", example = "Dell Latitude 5420")
    private String description;
}
