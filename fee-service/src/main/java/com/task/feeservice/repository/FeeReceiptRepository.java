package com.task.feeservice.repository;

import com.task.feeservice.entity.FeeReceipt;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeeReceiptRepository extends JpaRepository<FeeReceipt, UUID> {

    Optional<FeeReceipt> findById(UUID id);

    List<FeeReceipt> findByStudentIdOrderByCreatedAtDesc(String studentId);
}
