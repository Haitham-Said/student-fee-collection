package com.task.feeservice.controller;

import com.task.feeservice.dto.request.CollectFeeRequest;
import com.task.feeservice.dto.response.ReceiptResponse;
import com.task.feeservice.service.FeeService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/receipts")
public class FeeController {

    private final FeeService feeService;

    public FeeController(FeeService feeService) {
        this.feeService = feeService;
    }

    @PostMapping
    public ResponseEntity<ReceiptResponse> collectFee(@Valid @RequestBody CollectFeeRequest request) {
        ReceiptResponse created = feeService.collectFee(request);
        URI location = URI.create("/api/receipts/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceiptResponse> getReceipt(@PathVariable UUID id) {
        return ResponseEntity.ok(feeService.getReceiptById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ReceiptResponse>> getReceiptsByStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(feeService.getReceiptsByStudentId(studentId));
    }
}
