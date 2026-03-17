package com.task.studentservice.service.impl;

import com.task.studentservice.dto.request.CreateStudentRequest;
import com.task.studentservice.dto.response.InternalStudentLookupResponse;
import com.task.studentservice.dto.response.StudentResponse;
import com.task.studentservice.entity.Student;
import com.task.studentservice.exception.DuplicateStudentIdException;
import com.task.studentservice.exception.StudentNotFoundException;
import com.task.studentservice.mapper.StudentMapper;
import com.task.studentservice.repository.StudentRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    private static final String STUDENT_ID = "STU001";
    private static final Instant NOW = Instant.now();

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Nested
    @DisplayName("createStudent")
    class CreateStudent {

        @Test
        @DisplayName("creates student and returns response when studentId is new")
        void success() {
            CreateStudentRequest request = CreateStudentRequest.builder()
                    .studentId(STUDENT_ID)
                    .studentName("John Doe")
                    .grade("Grade 1")
                    .mobileNumber("+971501234567")
                    .schoolName("Test School")
                    .build();
            Student entity = new Student();
            entity.setId(1L);
            entity.setStudentId(STUDENT_ID);
            entity.setStudentName(request.studentName());
            entity.setGrade(request.grade());
            entity.setCreatedAt(NOW);
            entity.setUpdatedAt(NOW);
            StudentResponse response = StudentResponse.builder()
                    .id(1L)
                    .studentId(STUDENT_ID)
                    .studentName(request.studentName())
                    .grade(request.grade())
                    .build();

            when(studentRepository.existsByStudentId(STUDENT_ID)).thenReturn(false);
            when(studentMapper.toEntity(request)).thenReturn(entity);
            when(studentRepository.save(any(Student.class))).thenReturn(entity);
            when(studentMapper.toResponse(entity)).thenReturn(response);

            StudentResponse result = studentService.createStudent(request);

            assertThat(result).isEqualTo(response);
            verify(studentRepository).existsByStudentId(STUDENT_ID);
            verify(studentRepository).save(any(Student.class));
        }

        @Test
        @DisplayName("throws DuplicateStudentIdException when studentId already exists")
        void duplicateStudentId() {
            CreateStudentRequest request = CreateStudentRequest.builder()
                    .studentId(STUDENT_ID)
                    .studentName("John")
                    .grade("Grade 1")
                    .mobileNumber("+971501234567")
                    .schoolName("School")
                    .build();
            when(studentRepository.existsByStudentId(STUDENT_ID)).thenReturn(true);

            assertThatThrownBy(() -> studentService.createStudent(request))
                    .isInstanceOf(DuplicateStudentIdException.class)
                    .hasMessageContaining(STUDENT_ID);
            verify(studentRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getStudentByStudentId")
    class GetStudentByStudentId {

        @Test
        @DisplayName("returns student when found")
        void found() {
            Student entity = new Student();
            entity.setId(1L);
            entity.setStudentId(STUDENT_ID);
            entity.setStudentName("John");
            StudentResponse response = StudentResponse.builder().studentId(STUDENT_ID).studentName("John").build();
            when(studentRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.of(entity));
            when(studentMapper.toResponse(entity)).thenReturn(response);

            StudentResponse result = studentService.getStudentByStudentId(STUDENT_ID);

            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws StudentNotFoundException when not found")
        void notFound() {
            when(studentRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentService.getStudentByStudentId(STUDENT_ID))
                    .isInstanceOf(StudentNotFoundException.class)
                    .hasMessageContaining(STUDENT_ID);
            verify(studentMapper, never()).toResponse(any());
        }
    }

    @Nested
    @DisplayName("getAllStudents")
    class GetAllStudents {

        @Test
        @DisplayName("returns list of students from repository")
        void success() {
            Student entity = new Student();
            entity.setStudentId(STUDENT_ID);
            StudentResponse response = StudentResponse.builder().studentId(STUDENT_ID).build();
            when(studentRepository.findAll()).thenReturn(List.of(entity));
            when(studentMapper.toResponse(entity)).thenReturn(response);

            List<StudentResponse> result = studentService.getAllStudents();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).studentId()).isEqualTo(STUDENT_ID);
        }
    }

    @Nested
    @DisplayName("getInternalStudent")
    class GetInternalStudent {

        @Test
        @DisplayName("returns internal lookup when student found")
        void found() {
            Student entity = new Student();
            entity.setStudentId(STUDENT_ID);
            entity.setStudentName("John");
            entity.setGrade("Grade 1");
            entity.setSchoolName("School");
            InternalStudentLookupResponse response = InternalStudentLookupResponse.builder()
                    .studentId(STUDENT_ID)
                    .studentName("John")
                    .grade("Grade 1")
                    .schoolName("School")
                    .build();
            when(studentRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.of(entity));
            when(studentMapper.toInternalLookup(entity)).thenReturn(response);

            InternalStudentLookupResponse result = studentService.getInternalStudent(STUDENT_ID);

            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws StudentNotFoundException when not found")
        void notFound() {
            when(studentRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentService.getInternalStudent(STUDENT_ID))
                    .isInstanceOf(StudentNotFoundException.class)
                    .hasMessageContaining(STUDENT_ID);
            verify(studentMapper, never()).toInternalLookup(any());
        }
    }
}
