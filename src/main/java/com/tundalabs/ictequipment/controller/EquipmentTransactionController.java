package com.tundalabs.ictequipment.controller;

import com.tundalabs.ictequipment.dto.*;
import com.tundalabs.ictequipment.entity.EquipmentTransaction;
import com.tundalabs.ictequipment.service.EquipmentTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/equipment-transactions")
@RequiredArgsConstructor
@Slf4j
public class EquipmentTransactionController {

    private final EquipmentTransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponseDto>> createTransaction(
            @Valid @RequestBody CreateTransactionRequestDto request) {
        log.info("Creating new equipment transaction for staff ID: {}", request.getStaffId());
        TransactionResponseDto response = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Transaction created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> getTransactionById(@PathVariable Long id) {
        log.info("Fetching transaction by ID: {}", id);
        TransactionResponseDto response = transactionService.getTransactionById(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Transaction retrieved successfully", response));
    }

    @GetMapping("/code/{transactionCode}")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> getTransactionByCode(@PathVariable String transactionCode) {
        log.info("Fetching transaction by code: {}", transactionCode);
        TransactionResponseDto response = transactionService.getTransactionByCode(transactionCode);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Transaction retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransactionResponseDto>>> getTransactions(
            @RequestParam(required = false) Long staffId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable) {
        log.info("Fetching transactions with filters - staffId: {}, status: {}", staffId, status);

        TransactionFilterParams filters = TransactionFilterParams.builder()
                .staffId(staffId)
                .status(status != null ? EquipmentTransaction.TransactionStatus.valueOf(status) : null)
                .startDate(null)
                .endDate(null)
                .build();

        Page<TransactionResponseDto> response = transactionService.getTransactions(filters, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Transactions retrieved successfully", response));
    }

    @PostMapping("/{id}/sign")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> submitSignatures(
            @PathVariable Long id,
            @Valid @RequestBody SubmitSignatureRequestDto request) {
        log.info("Submitting signatures for transaction ID: {}", id);
        TransactionResponseDto response = transactionService.submitSignatures(id, request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Signatures submitted successfully", response));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> cancelTransaction(@PathVariable Long id) {
        log.info("Cancelling transaction ID: {}", id);
        TransactionResponseDto response = transactionService.cancelTransaction(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Transaction cancelled successfully", response));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generateTransactionPdf(@PathVariable Long id) {
        log.info("Generating PDF for transaction ID: {}", id);
        byte[] pdfBytes = transactionService.generateTransactionPdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "transaction_" + id + ".pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
