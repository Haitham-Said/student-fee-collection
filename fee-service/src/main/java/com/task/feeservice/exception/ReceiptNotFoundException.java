package com.task.feeservice.exception;

import java.util.UUID;

public class ReceiptNotFoundException extends RuntimeException {

    public ReceiptNotFoundException(UUID id) {
        super("Receipt with id " + id + " was not found");
    }
}
