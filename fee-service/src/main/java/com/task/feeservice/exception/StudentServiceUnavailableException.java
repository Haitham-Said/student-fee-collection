package com.task.feeservice.exception;

public class StudentServiceUnavailableException extends RuntimeException {

    public StudentServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
