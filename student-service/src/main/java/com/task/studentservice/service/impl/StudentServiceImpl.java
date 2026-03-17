package com.task.studentservice.service.impl;

import com.task.studentservice.dto.request.CreateStudentRequest;
import com.task.studentservice.dto.response.InternalStudentLookupResponse;
import com.task.studentservice.dto.response.StudentResponse;
import com.task.studentservice.entity.Student;
import com.task.studentservice.exception.DuplicateStudentIdException;
import com.task.studentservice.exception.StudentNotFoundException;
import com.task.studentservice.mapper.StudentMapper;
import com.task.studentservice.repository.StudentRepository;
import com.task.studentservice.service.StudentService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public StudentServiceImpl(StudentRepository studentRepository, StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
    }

    @Override
    @Transactional
    public StudentResponse createStudent(CreateStudentRequest request) {
        if (studentRepository.existsByStudentId(request.studentId())) {
            throw new DuplicateStudentIdException(request.studentId());
        }
        Student student = studentMapper.toEntity(request);
        Student saved = studentRepository.save(student);
        return studentMapper.toResponse(saved);
    }

    @Override
    public StudentResponse getStudentByStudentId(String studentId) {
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        return studentMapper.toResponse(student);
    }

    @Override
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(studentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InternalStudentLookupResponse getInternalStudent(String studentId) {
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        return studentMapper.toInternalLookup(student);
    }
}
