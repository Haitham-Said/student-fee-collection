package com.task.feeservice.dto.request;

import com.task.feeservice.enums.FeeType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CollectFeeRequest(
        @NotBlank String studentId,
        @NotBlank String recipientName,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amountPaid,
        @NotNull FeeType feeType,
        String currency,
        String cardNumberMasked,
        String cardType
) {
}
