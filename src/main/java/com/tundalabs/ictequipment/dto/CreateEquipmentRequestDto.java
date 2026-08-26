package com.tundalabs.ictequipment.dto;

import com.tundalabs.ictequipment.entity.EquipmentRequest;
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
@Schema(description = "Request DTO for staff to submit equipment request")
public class CreateEquipmentRequestDto {

    @NotNull(message = "Request type is required")
    @Schema(description = "Type of request: ISSUE, RETURN, or EXCHANGE", example = "EXCHANGE")
    private EquipmentRequest.RequestType requestType;

    @NotBlank(message = "Reason is required")
    @Schema(description = "Reason for the request", example = "Need to replace old laptop with new one")
    private String reason;

    @Schema(description = "Asset number of equipment to return (for RETURN or EXCHANGE requests)", example = "AST-2023-001")
    private String returnAssetNumber;

    @Schema(description = "Asset number of equipment to issue (for ISSUE or EXCHANGE requests)", example = "AST-2024-001")
    private String issueAssetNumber;

    @Schema(description = "Preferred equipment type (for ISSUE or EXCHANGE requests)", example = "Laptop")
    private String preferredEquipmentType;
}
