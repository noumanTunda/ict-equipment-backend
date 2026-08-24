package com.tundalabs.ictequipment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitSignatureRequestDto {
    @NotBlank(message = "Employee signature is required")
    private String employeeSignature;

    @NotBlank(message = "Officer signature is required")
    private String officerSignature;
}
