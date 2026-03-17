package com.task.studentservice.service;

import com.task.studentservice.dto.request.CreateStudentRequest;
import com.task.studentservice.dto.response.InternalStudentLookupResponse;
import com.task.studentservice.dto.response.StudentResponse;
import java.util.List;

public interface StudentService {

    StudentResponse createStudent(CreateStudentRequest request);

    StudentResponse getStudentByStudentId(String studentId);

    List<StudentResponse> getAllStudents();

    InternalStudentLookupResponse getInternalStudent(String studentId);
}
