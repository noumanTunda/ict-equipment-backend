package com.tundalabs.ictequipment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for user search results")
public class UserSearchResponseDto {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "Employee ID", example = "EMP001")
    private String employeeId;

    @Schema(description = "Full name of the user", example = "John Doe")
    private String fullName;
}
