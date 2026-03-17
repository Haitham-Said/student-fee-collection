package com.task.studentservice.mapper;

import com.task.studentservice.dto.request.CreateStudentRequest;
import com.task.studentservice.dto.response.InternalStudentLookupResponse;
import com.task.studentservice.dto.response.StudentResponse;
import com.task.studentservice.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public Student toEntity(CreateStudentRequest request) {
        Student student = new Student();
        student.setStudentId(request.studentId());
        student.setStudentName(request.studentName());
        student.setGrade(request.grade());
        student.setMobileNumber(request.mobileNumber());
        student.setSchoolName(request.schoolName());
        return student;
    }

    public StudentResponse toResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .studentId(student.getStudentId())
                .studentName(student.getStudentName())
                .grade(student.getGrade())
                .mobileNumber(student.getMobileNumber())
                .schoolName(student.getSchoolName())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
    }

    public InternalStudentLookupResponse toInternalLookup(Student student) {
        return InternalStudentLookupResponse.builder()
                .studentId(student.getStudentId())
                .studentName(student.getStudentName())
                .grade(student.getGrade())
                .schoolName(student.getSchoolName())
                .build();
    }
}
