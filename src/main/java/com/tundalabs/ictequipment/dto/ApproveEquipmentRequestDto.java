package com.tundalabs.ictequipment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for approving equipment request with ICT checklist")
public class ApproveEquipmentRequestDto {

    @NotNull(message = "Request ID is required")
    @Schema(description = "Equipment request ID to approve", example = "1")
    private Long requestId;

    @Schema(description = "Asset number of equipment to issue (for ISSUE or EXCHANGE approvals)", example = "AST-2024-001")
    private String issueAssetNumber;

    @Schema(description = "Asset number of equipment to return (for RETURN or EXCHANGE approvals)", example = "AST-2023-001")
    private String returnAssetNumber;

    @Valid
    @Schema(description = "ICT configuration checklist for the equipment (required for ISSUE and EXCHANGE requests)")
    private IctChecklistRequestDto checklist;

    @Schema(description = "Accessories provided with issued equipment")
    private List<String> accessoriesProvided;

    @Schema(description = "Condition of returned equipment", example = "Good working condition")
    private String returnCondition;

    @Schema(description = "Remarks for returned equipment", example = "Minor scratches on lid")
    private String returnRemarks;
}
