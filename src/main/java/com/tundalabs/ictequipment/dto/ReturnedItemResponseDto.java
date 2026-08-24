package com.tundalabs.ictequipment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnedItemResponseDto {
    private Long id;
    private String assetNumber;
    private String serialNumber;
    private String equipmentType;
    private String itemCondition;
    private String remarks;
}
