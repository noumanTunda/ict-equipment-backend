package com.tundalabs.ictequipment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssuedItemResponseDto {
    private Long id;
    private String assetNumber;
    private String serialNumber;
    private String equipmentType;
    private List<String> accessoriesProvided;
}
