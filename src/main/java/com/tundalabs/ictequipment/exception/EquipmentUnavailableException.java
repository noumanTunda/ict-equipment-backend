package com.tundalabs.ictequipment.exception;

public class EquipmentUnavailableException extends RuntimeException {
    public EquipmentUnavailableException(String message) {
        super(message);
    }

    public EquipmentUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
