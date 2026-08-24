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
    @NotBlank(message = "Staff ID is required")
    @Size(max = 30, message = "Staff ID must not exceed 30 characters")
    private String staffId;

    @NotBlank(message = "Issuing officer ID is required")
    @Size(max = 30, message = "Issuing officer ID must not exceed 30 characters")
    private String issuingOfficerId;

    @Valid
    @NotEmpty(message = "At least one issued item is required")
    private List<IssuedItemRequestDto> issuedItems;

    @Valid
    private List<ReturnedItemRequestDto> returnedItems;

    @Valid
    private IctChecklistRequestDto checklist;
}
