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
public class ReturnedItemRequestDto {
    @NotBlank(message = "Asset number is required")
    @Size(max = 50, message = "Asset number must not exceed 50 characters")
    private String assetNumber;

    @NotBlank(message = "Item condition is required")
    @Size(max = 100, message = "Item condition must not exceed 100 characters")
    private String itemCondition;

    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
}
