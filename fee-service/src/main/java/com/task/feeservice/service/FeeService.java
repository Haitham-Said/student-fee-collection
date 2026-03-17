package com.task.feeservice.service;

import com.task.feeservice.dto.request.CollectFeeRequest;
import com.task.feeservice.dto.response.ReceiptResponse;
import java.util.List;
import java.util.UUID;

public interface FeeService {

    ReceiptResponse collectFee(CollectFeeRequest request);

    ReceiptResponse getReceiptById(UUID id);

    List<ReceiptResponse> getReceiptsByStudentId(String studentId);
}
