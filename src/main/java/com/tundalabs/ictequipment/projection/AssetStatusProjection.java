package com.tundalabs.ictequipment.projection;

import com.tundalabs.ictequipment.entity.Equipment;
import com.tundalabs.ictequipment.entity.EquipmentTransaction;

import java.time.LocalDateTime;

public interface AssetStatusProjection {

    Long getId();

    String getAssetNumber();

    String getSerialNumber();

    String getEquipmentType();

    Equipment.EquipmentStatus getStatus();

    LocalDateTime getIssuedAt();

    LocalDateTime getReturnedAt();

    EquipmentTransaction.TransactionStatus getTransactionStatus();

    String getTransactionCode();

    Long getStaffId();
}
