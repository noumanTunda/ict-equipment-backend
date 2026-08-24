package com.tundalabs.ictequipment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    @NotBlank(message = "Employee ID is required")
    @Size(max = 30, message = "Employee ID must not exceed 30 characters")
    private String employeeId;

    @NotBlank(message = "Full name is required")
    @Size(max = 120, message = "Full name must not exceed 120 characters")
    private String fullName;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Department is required")
    @Size(max = 80, message = "Department must not exceed 80 characters")
    private String department;

    @NotBlank(message = "Mobile number is required")
    @Size(max = 20, message = "Mobile number must not exceed 20 characters")
    private String mobileNo;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotBlank(message = "Role is required")
    private String role;
}
