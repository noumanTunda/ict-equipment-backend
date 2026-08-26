package com.tundalabs.ictequipment.dto;

import com.tundalabs.ictequipment.entity.Equipment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for equipment details")
public class EquipmentResponseDto {
    
    @Schema(description = "Equipment ID", example = "1")
    private Long id;

    @Schema(description = "Unique asset number", example = "AST-2024-001")
    private String assetNumber;

    @Schema(description = "Unique serial number", example = "SN123456789")
    private String serialNumber;

    @Schema(description = "Type of equipment", example = "Laptop")
    private String equipmentType;

    @Schema(description = "Supplier of equipment", example = "Supplier Name")
    private String supplierDetails;

    @Schema(description = "Brand or Model of equipment", example = "HP Laserjet")
    private String brandModel;

    @Schema(description = "Equipment status", example = "AVAILABLE")
    private Equipment.EquipmentStatus status;

    @Schema(description = "Equipment description", example = "Dell Latitude 5420")
    private String description;

    @Schema(description = "Equipment creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Equipment last update timestamp")
    private LocalDateTime updatedAt;
}
