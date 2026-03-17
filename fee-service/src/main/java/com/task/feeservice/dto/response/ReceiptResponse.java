package com.task.feeservice.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ReceiptResponse(
        UUID id,
        String referenceNumber,
        Instant paymentDateTime,
        String studentId,
        String studentName,
        String schoolName,
        String recipientName,
        String cardNumberMasked,
        String cardType,
        String transactionStatus,
        List<ReceiptLineItem> lineItems,
        BigDecimal totalAmount,
        String currency,
        Instant createdAt
) {
}
