package com.task.studentservice.dto.response;

import lombok.Builder;

@Builder
public record InternalStudentLookupResponse(
        String studentId,
        String studentName,
        String grade,
        String schoolName
) {
}
