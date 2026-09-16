package com.tundalabs.ictequipment.exception;

public class InvalidEquipmentStateException extends RuntimeException {
    public InvalidEquipmentStateException(String message) {
        super(message);
    }

    public InvalidEquipmentStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
