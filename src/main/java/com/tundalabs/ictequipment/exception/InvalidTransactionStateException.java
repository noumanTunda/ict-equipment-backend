package com.tundalabs.ictequipment.exception;

public class InvalidTransactionStateException extends RuntimeException {
    public InvalidTransactionStateException(String message) {
        super(message);
    }

    public InvalidTransactionStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
