package com.task.feeservice.service.impl;

import com.task.feeservice.client.StudentServiceClient;
import com.task.feeservice.client.dto.StudentLookupDto;
import com.task.feeservice.dto.request.CollectFeeRequest;
import com.task.feeservice.dto.response.ReceiptLineItem;
import com.task.feeservice.dto.response.ReceiptResponse;
import com.task.feeservice.entity.FeeReceipt;
import com.task.feeservice.enums.FeeType;
import com.task.feeservice.enums.ReceiptStatus;
import com.task.feeservice.exception.ReceiptNotFoundException;
import com.task.feeservice.repository.FeeReceiptRepository;
import com.task.feeservice.service.FeeService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeeServiceImpl implements FeeService {

    private final FeeReceiptRepository feeReceiptRepository;
    private final StudentServiceClient studentServiceClient;

    public FeeServiceImpl(FeeReceiptRepository feeReceiptRepository,
                          StudentServiceClient studentServiceClient) {
        this.feeReceiptRepository = feeReceiptRepository;
        this.studentServiceClient = studentServiceClient;
    }

    @Override
    @Transactional
    public ReceiptResponse collectFee(CollectFeeRequest request) {
        StudentLookupDto student = studentServiceClient.getStudentById(request.studentId());

        String referenceNumber = generateReferenceNumber();

        FeeReceipt receipt = new FeeReceipt();
        receipt.setReferenceNumber(referenceNumber);
        receipt.setPaymentDateTime(Instant.now());
        receipt.setStudentId(request.studentId());
        receipt.setStudentNameSnapshot(student.studentName());
        receipt.setGradeSnapshot(student.grade());
        receipt.setSchoolNameSnapshot(student.schoolName());
        receipt.setRecipientName(request.recipientName());
        receipt.setAmountPaid(request.amountPaid());
        receipt.setCurrency(request.currency());
        receipt.setCardNumberMasked(request.cardNumberMasked());
        receipt.setCardType(request.cardType());
        receipt.setFeeType(request.feeType());
        receipt.setStatus(ReceiptStatus.PAID);

        FeeReceipt saved = feeReceiptRepository.save(receipt);
        return toResponse(saved);
    }

    @Override
    public ReceiptResponse getReceiptById(UUID id) {
        FeeReceipt receipt = feeReceiptRepository.findById(id)
                .orElseThrow(() -> new ReceiptNotFoundException(id));
        return toResponse(receipt);
    }

    @Override
    public List<ReceiptResponse> getReceiptsByStudentId(String studentId) {
        return feeReceiptRepository.findByStudentIdOrderByCreatedAtDesc(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ReceiptResponse toResponse(FeeReceipt receipt) {
        ReceiptLineItem lineItem = ReceiptLineItem.builder()
                .itemName(getFeeTypeDisplayName(receipt.getFeeType()))
                .description("1 x " + receipt.getGradeSnapshot())
                .amount(receipt.getAmountPaid())
                .currency(receipt.getCurrency())
                .build();

        return ReceiptResponse.builder()
                .id(receipt.getId())
                .referenceNumber(receipt.getReferenceNumber())
                .paymentDateTime(receipt.getPaymentDateTime())
                .studentId(receipt.getStudentId())
                .studentName(receipt.getStudentNameSnapshot())
                .schoolName(receipt.getSchoolNameSnapshot())
                .recipientName(receipt.getRecipientName())
                .cardNumberMasked(receipt.getCardNumberMasked())
                .cardType(receipt.getCardType())
                .transactionStatus(receipt.getStatus() == ReceiptStatus.PAID ? "Successful" : receipt.getStatus().name())
                .lineItems(List.of(lineItem))
                .totalAmount(receipt.getAmountPaid())
                .currency(receipt.getCurrency())
                .createdAt(receipt.getCreatedAt())
                .build();
    }

    private String getFeeTypeDisplayName(FeeType feeType) {
        return switch (feeType) {
            case TUITION -> "Tuition Fees";
            case TRANSPORT -> "Transport Fees";
            case EXAM -> "Exam Fees";
            case OTHER -> "Other Fees";
        };
    }

    private String generateReferenceNumber() {
        return String.format("%012d", Math.abs((System.currentTimeMillis() + (long) (Math.random() * 1000)) % 1000000000000L));
    }
}
