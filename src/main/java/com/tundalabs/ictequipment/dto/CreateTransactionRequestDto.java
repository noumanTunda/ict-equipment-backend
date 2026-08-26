package com.tundalabs.ictequipment.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequestDto {
    @NotNull(message = "Staff ID is required")
    private Long staffId;

    @NotNull(message = "Issuing officer ID is required")
    private Long issuingOfficerId;

    @Valid
    @NotEmpty(message = "At least one issued item is required")
    private List<IssuedItemRequestDto> issuedItems;

    @Valid
    private List<ReturnedItemRequestDto> returnedItems;

    @Valid
    private IctChecklistRequestDto checklist;
}
