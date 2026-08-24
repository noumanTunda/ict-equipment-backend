package com.tundalabs.ictequipment.dto;

import com.tundalabs.ictequipment.entity.EquipmentTransaction.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFilterParams {
    private String staffId;
    private TransactionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
