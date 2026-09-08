package com.tundalabs.ictequipment.dto;

import jakarta.validation.constraints.NotBlank;
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
public class IssuedItemRequestDto {
    @NotBlank(message = "Asset number is required")
    @Size(max = 50, message = "Asset number must not exceed 50 characters")
    private String assetNumber;

    private List<String> accessoriesProvided;
}
