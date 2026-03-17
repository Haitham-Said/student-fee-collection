package com.task.feeservice.dto.response;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record ReceiptLineItem(
        String itemName,
        String description,
        BigDecimal amount,
        String currency
) {
}
