package com.task.studentservice.exception;

public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(String studentId) {
        super("Student with id " + studentId + " was not found");
    }
}
