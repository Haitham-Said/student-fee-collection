package com.task.studentservice.dto.response;

import java.time.Instant;
import lombok.Builder;

@Builder
public record StudentResponse(
        Long id,
        String studentId,
        String studentName,
        String grade,
        String mobileNumber,
        String schoolName,
        Instant createdAt,
        Instant updatedAt
) {
}
