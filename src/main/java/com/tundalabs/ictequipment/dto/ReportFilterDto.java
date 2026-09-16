package com.tundalabs.ictequipment.dto;

import com.tundalabs.ictequipment.entity.Equipment;
import com.tundalabs.ictequipment.entity.EquipmentTransaction;
import com.tundalabs.ictequipment.entity.TransactionReturnedItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Filter parameters for dynamic report generation")
public class ReportFilterDto {

    @Schema(description = "Start date for temporal filtering", example = "2024-01-01T00:00:00")
    private LocalDateTime startDate;

    @Schema(description = "End date for temporal filtering", example = "2024-12-31T23:59:59")
    private LocalDateTime endDate;

    @Schema(description = "Filter by department", example = "ICT")
    private Equipment.EquipmentDepartment department;

    @Schema(description = "Filter by equipment category/type", example = "Laptop")
    private String equipmentCategory;

    @Schema(description = "Filter by transaction status", example = "COMPLETED")
    private EquipmentTransaction.TransactionStatus transactionStatus;

    @Schema(description = "Filter by item condition", example = "GOOD")
    private TransactionReturnedItem.ItemCondition itemCondition;

    @Schema(description = "Overdue duration threshold in days", example = "30")
    private Integer overdueThresholdDays;

    @Schema(description = "List of staff IDs to filter", example = "[1, 2, 3]")
    private List<Long> staffIds;

    @Schema(description = "Include only equipment with warranty", example = "true")
    private Boolean hasWarranty;

    @Schema(description = "Filter by equipment status", example = "ISSUED")
    private Equipment.EquipmentStatus equipmentStatus;

    @Schema(description = "Search query for asset number or serial number", example = "AST-2024")
    private String searchQuery;
}
