package com.tundalabs.ictequipment.dto;

import com.tundalabs.ictequipment.entity.Equipment;
import com.tundalabs.ictequipment.entity.EquipmentTransaction;
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
@Schema(description = "Response DTO for asset operational status with temporal markers")
public class AssetStatusResponseDto {

    @Schema(description = "Equipment ID", example = "1")
    private Long id;

    @Schema(description = "Asset number", example = "AST-2024-001")
    private String assetNumber;

    @Schema(description = "Serial number", example = "SN123456789")
    private String serialNumber;

    @Schema(description = "Equipment type", example = "Laptop")
    private String equipmentType;

    @Schema(description = "Current operational status", example = "ISSUED")
    private Equipment.EquipmentStatus status;

    @Schema(description = "Issuance timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime issuedAt;

    @Schema(description = "Return timestamp (if returned)", example = "2024-06-15T14:00:00")
    private LocalDateTime returnedAt;

    @Schema(description = "Transaction status", example = "COMPLETED")
    private EquipmentTransaction.TransactionStatus transactionStatus;

    @Schema(description = "Transaction code", example = "TXN-20240115103000-ABC123")
    private String transactionCode;

    @Schema(description = "Staff ID who was assigned the asset", example = "123")
    private Long staffId;
}
