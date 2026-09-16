package com.tundalabs.ictequipment.dto;

import com.tundalabs.ictequipment.entity.Equipment;
import com.tundalabs.ictequipment.entity.TransactionReturnedItem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for equipment re-inspection after return")
public class EquipmentInspectionDto {

    @NotNull(message = "Inspection result is required")
    @Schema(description = "Target status after inspection: AVAILABLE or MAINTENANCE", example = "AVAILABLE")
    private Equipment.EquipmentStatus targetStatus;

    @NotNull(message = "Item condition is required")
    @Schema(description = "Condition of returned equipment", example = "GOOD")
    private TransactionReturnedItem.ItemCondition itemCondition;

    @Schema(description = "Detailed inspection remarks", example = "Equipment in good working condition, no defects found")
    private String remarks;

    @Schema(description = "Maintenance notes (required when targetStatus is MAINTENANCE)", example = "Screen flickering, needs replacement")
    private String maintenanceNotes;

    @Schema(description = "Estimated maintenance cost (optional)", example = "150.00")
    private Double estimatedMaintenanceCost;
}
