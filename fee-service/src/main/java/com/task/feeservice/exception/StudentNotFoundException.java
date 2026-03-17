package com.task.feeservice.exception;

public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(String studentId) {
        super("Student with id " + studentId + " was not found");
    }
}
