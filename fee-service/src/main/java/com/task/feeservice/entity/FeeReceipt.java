package com.task.feeservice.entity;

import com.task.feeservice.enums.FeeType;
import com.task.feeservice.enums.ReceiptStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "fee_receipts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_fee_receipts_reference_number", columnNames = "reference_number")
        },
        indexes = {
                @Index(name = "ix_fee_receipts_reference_number", columnList = "reference_number"),
                @Index(name = "ix_fee_receipts_student_id", columnList = "student_id")
        }
)
public class FeeReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "reference_number", nullable = false, length = 50)
    private String referenceNumber;

    @Column(name = "payment_date_time", nullable = false)
    private Instant paymentDateTime;

    @Column(name = "student_id", nullable = false, length = 50)
    private String studentId;

    @Column(name = "student_name_snapshot", nullable = false, length = 200)
    private String studentNameSnapshot;

    @Column(name = "grade_snapshot", nullable = false, length = 50)
    private String gradeSnapshot;

    @Column(name = "school_name_snapshot", nullable = false, length = 200)
    private String schoolNameSnapshot;

    @Column(name = "recipient_name", nullable = false, length = 200)
    private String recipientName;

    @Column(name = "amount_paid", nullable = false, precision = 15, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Column(name = "card_number_masked", length = 30)
    private String cardNumberMasked;

    @Column(name = "card_type", length = 30)
    private String cardType;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false, length = 20)
    private FeeType feeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReceiptStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
        if (this.paymentDateTime == null) {
            this.paymentDateTime = this.createdAt;
        }
    }
}
