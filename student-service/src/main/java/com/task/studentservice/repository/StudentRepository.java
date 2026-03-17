package com.task.studentservice.repository;

import com.task.studentservice.entity.Student;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentId(String studentId);

    boolean existsByStudentId(String studentId);
}
