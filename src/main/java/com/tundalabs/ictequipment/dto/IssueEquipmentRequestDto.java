package com.tundalabs.ictequipment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
@Schema(description = "Request DTO for issuing ICT equipment to staff")
public class IssueEquipmentRequestDto {

    @NotNull(message = "Staff ID is required")
    @Schema(description = "ID of the staff member receiving equipment", example = "1")
    private Long staffId;

    @NotNull(message = "Issuing officer ID is required")
    @Schema(description = "ID of the ICT officer issuing the equipment", example = "2")
    private Long issuingOfficerId;

    @Valid
    @NotEmpty(message = "At least one item to issue is required")
    @Schema(description = "List of equipment items to be issued")
    private List<IssuedItemRequestDto> issuedItems;

    @Valid
    @NotNull(message = "ICT checklist is required for equipment issuance")
    @Schema(description = "ICT configuration checklist for the equipment")
    private IctChecklistRequestDto checklist;
}
