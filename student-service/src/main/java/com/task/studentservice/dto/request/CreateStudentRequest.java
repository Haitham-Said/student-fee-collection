package com.task.studentservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateStudentRequest(
        @NotBlank String studentId,
        @NotBlank String studentName,
        @NotBlank String grade,
        @NotBlank String mobileNumber,
        @NotBlank String schoolName
) {
}
