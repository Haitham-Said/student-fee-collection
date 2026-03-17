package com.task.feeservice.client.dto;

import lombok.Builder;

@Builder
public record StudentLookupDto(
        String studentId,
        String studentName,
        String grade,
        String schoolName
) {
}
