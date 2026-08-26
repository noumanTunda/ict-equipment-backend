package com.tundalabs.ictequipment.dto;

import com.tundalabs.ictequipment.entity.EquipmentTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDto {
    private Long id;
    private String transactionCode;
    private Long staffId;
    private String staffName;
    private Long issuingOfficerId;
    private String issuingOfficerName;
    private String status;
    private String employeeSignature;
    private String officerSignature;
    private LocalDateTime employeeSignedAt;
    private LocalDateTime officerSignedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<IssuedItemResponseDto> issuedItems;
    private List<ReturnedItemResponseDto> returnedItems;
    private IctChecklistResponseDto checklist;
}
