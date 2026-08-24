package com.tundalabs.ictequipment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long id;
    private String employeeId;
    private String fullName;
    private String email;
    private String department;
    private String role;
    private String status;
}
