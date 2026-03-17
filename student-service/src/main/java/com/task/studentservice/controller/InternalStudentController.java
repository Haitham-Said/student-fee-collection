package com.task.studentservice.controller;

import com.task.studentservice.dto.response.InternalStudentLookupResponse;
import com.task.studentservice.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/students")
public class InternalStudentController {

    private final StudentService studentService;

    public InternalStudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<InternalStudentLookupResponse> getInternalStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(studentService.getInternalStudent(studentId));
    }
}
