package com.tundalabs.ictequipment.dto;

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
public class IssuedItemRequestDto {
    @NotBlank(message = "Asset number is required")
    @Size(max = 50, message = "Asset number must not exceed 50 characters")
    private String assetNumber;

    @Size(max = 500, message = "Accessories provided must not exceed 500 characters")
    private String accessoriesProvided;
}
