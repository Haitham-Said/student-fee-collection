package com.task.feeservice.service.impl;

import com.task.feeservice.client.StudentServiceClient;
import com.task.feeservice.client.dto.StudentLookupDto;
import com.task.feeservice.dto.request.CollectFeeRequest;
import com.task.feeservice.dto.response.ReceiptResponse;
import com.task.feeservice.entity.FeeReceipt;
import com.task.feeservice.enums.FeeType;
import com.task.feeservice.enums.ReceiptStatus;
import com.task.feeservice.exception.ReceiptNotFoundException;
import com.task.feeservice.exception.StudentNotFoundException;
import com.task.feeservice.repository.FeeReceiptRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeeServiceImplTest {

    private static final String STUDENT_ID = "STU001";
    private static final String REFERENCE_NUMBER = "123456789012";
    private static final UUID RECEIPT_UUID = UUID.randomUUID();
    private static final Instant NOW = Instant.now();

    @Mock
    private FeeReceiptRepository feeReceiptRepository;

    @Mock
    private StudentServiceClient studentServiceClient;

    @InjectMocks
    private FeeServiceImpl feeService;

    @Nested
    @DisplayName("collectFee")
    class CollectFee {

        @Test
        @DisplayName("fetches student, saves receipt and returns response")
        void success() {
            StudentLookupDto student = StudentLookupDto.builder()
                    .studentId(STUDENT_ID)
                    .studentName("John Doe")
                    .grade("Grade 1")
                    .schoolName("Test School")
                    .build();
            CollectFeeRequest request = CollectFeeRequest.builder()
                    .studentId(STUDENT_ID)
                    .recipientName("Parent Name")
                    .amountPaid(new BigDecimal("100.50"))
                    .feeType(FeeType.TUITION)
                    .currency("AED")
                    .cardNumberMasked("****0081")
                    .cardType("MasterCard")
                    .build();
            FeeReceipt savedReceipt = new FeeReceipt();
            savedReceipt.setId(RECEIPT_UUID);
            savedReceipt.setReferenceNumber(REFERENCE_NUMBER);
            savedReceipt.setPaymentDateTime(NOW);
            savedReceipt.setStudentId(STUDENT_ID);
            savedReceipt.setStudentNameSnapshot(student.studentName());
            savedReceipt.setGradeSnapshot(student.grade());
            savedReceipt.setSchoolNameSnapshot(student.schoolName());
            savedReceipt.setRecipientName(request.recipientName());
            savedReceipt.setAmountPaid(request.amountPaid());
            savedReceipt.setCurrency("AED");
            savedReceipt.setFeeType(FeeType.TUITION);
            savedReceipt.setStatus(ReceiptStatus.PAID);
            savedReceipt.setCreatedAt(NOW);

            when(studentServiceClient.getStudentById(STUDENT_ID)).thenReturn(student);
            when(feeReceiptRepository.save(any(FeeReceipt.class))).thenAnswer(inv -> {
                FeeReceipt r = inv.getArgument(0);
                r.setId(RECEIPT_UUID);
                r.setReferenceNumber(REFERENCE_NUMBER);
                r.setCreatedAt(NOW);
                return r;
            });

            ReceiptResponse result = feeService.collectFee(request);

            assertThat(result.id()).isEqualTo(RECEIPT_UUID);
            assertThat(result.studentId()).isEqualTo(STUDENT_ID);
            assertThat(result.studentName()).isEqualTo("John Doe");
            assertThat(result.recipientName()).isEqualTo("Parent Name");
            assertThat(result.totalAmount()).isEqualByComparingTo("100.50");
            assertThat(result.currency()).isEqualTo("AED");
            assertThat(result.transactionStatus()).isEqualTo("Successful");
            assertThat(result.lineItems()).hasSize(1);
            assertThat(result.lineItems().get(0).itemName()).isEqualTo("Tuition Fees");
            assertThat(result.lineItems().get(0).description()).isEqualTo("1 x Grade 1");
            verify(studentServiceClient).getStudentById(STUDENT_ID);
            verify(feeReceiptRepository).save(any(FeeReceipt.class));
        }

        @Test
        @DisplayName("propagates StudentNotFoundException when student not found")
        void studentNotFound() {
            CollectFeeRequest request = CollectFeeRequest.builder()
                    .studentId(STUDENT_ID)
                    .recipientName("Parent")
                    .amountPaid(BigDecimal.ONE)
                    .feeType(FeeType.TUITION)
                    .currency("AED")
                    .build();
            when(studentServiceClient.getStudentById(STUDENT_ID)).thenThrow(new StudentNotFoundException(STUDENT_ID));

            assertThatThrownBy(() -> feeService.collectFee(request))
                    .isInstanceOf(StudentNotFoundException.class)
                    .hasMessageContaining(STUDENT_ID);
            verify(feeReceiptRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getReceiptById")
    class GetReceiptById {

        @Test
        @DisplayName("returns receipt when found")
        void found() {
            FeeReceipt receipt = new FeeReceipt();
            receipt.setId(RECEIPT_UUID);
            receipt.setReferenceNumber(REFERENCE_NUMBER);
            receipt.setStudentId(STUDENT_ID);
            receipt.setFeeType(FeeType.TUITION);
            receipt.setStatus(ReceiptStatus.PAID);
            receipt.setCurrency("AED");
            receipt.setAmountPaid(BigDecimal.TEN);
            receipt.setGradeSnapshot("Grade 1");
            when(feeReceiptRepository.findById(RECEIPT_UUID)).thenReturn(Optional.of(receipt));

            ReceiptResponse result = feeService.getReceiptById(RECEIPT_UUID);

            assertThat(result.id()).isEqualTo(RECEIPT_UUID);
            assertThat(result.referenceNumber()).isEqualTo(REFERENCE_NUMBER);
            assertThat(result.studentId()).isEqualTo(STUDENT_ID);
        }

        @Test
        @DisplayName("throws ReceiptNotFoundException when not found")
        void notFound() {
            when(feeReceiptRepository.findById(RECEIPT_UUID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> feeService.getReceiptById(RECEIPT_UUID))
                    .isInstanceOf(ReceiptNotFoundException.class)
                    .hasMessageContaining(RECEIPT_UUID.toString());
        }
    }

    @Nested
    @DisplayName("getReceiptsByStudentId")
    class GetReceiptsByStudentId {

        @Test
        @DisplayName("returns list of receipts for student")
        void success() {
            FeeReceipt receipt = new FeeReceipt();
            receipt.setId(RECEIPT_UUID);
            receipt.setReferenceNumber(REFERENCE_NUMBER);
            receipt.setStudentId(STUDENT_ID);
            receipt.setFeeType(FeeType.TUITION);
            receipt.setStatus(ReceiptStatus.PAID);
            receipt.setCurrency("AED");
            receipt.setAmountPaid(BigDecimal.TEN);
            receipt.setGradeSnapshot("Grade 1");
            receipt.setStudentNameSnapshot("John");
            receipt.setSchoolNameSnapshot("School");
            receipt.setRecipientName("Parent");
            receipt.setPaymentDateTime(NOW);
            receipt.setCreatedAt(NOW);
            when(feeReceiptRepository.findByStudentIdOrderByCreatedAtDesc(STUDENT_ID)).thenReturn(List.of(receipt));

            List<ReceiptResponse> result = feeService.getReceiptsByStudentId(STUDENT_ID);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).studentId()).isEqualTo(STUDENT_ID);
            verify(feeReceiptRepository).findByStudentIdOrderByCreatedAtDesc(STUDENT_ID);
        }
    }
}
