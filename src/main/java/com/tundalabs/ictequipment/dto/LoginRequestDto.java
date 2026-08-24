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
public class LoginRequestDto {
    @NotBlank(message = "Employee ID or Email is required")
    private String employeeIdOrEmail;

    @NotBlank(message = "Password is required")
    private String password;
}
