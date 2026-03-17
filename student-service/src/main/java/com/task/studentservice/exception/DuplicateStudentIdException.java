package com.task.studentservice.exception;

public class DuplicateStudentIdException extends RuntimeException {

    public DuplicateStudentIdException(String studentId) {
        super("Student with id " + studentId + " already exists");
    }
}
