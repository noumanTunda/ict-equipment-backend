package com.tundalabs.ictequipment.controller;

import com.tundalabs.ictequipment.dto.*;
import com.tundalabs.ictequipment.entity.EquipmentTransaction;
import com.tundalabs.ictequipment.service.EquipmentTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/equipment-transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Equipment Transactions", description = "APIs for managing ICT equipment transactions")
@SecurityRequirement(name = "bearerAuth")
public class EquipmentTransactionController {

    private final EquipmentTransactionService transactionService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ICT_OFFICER', 'ROLE_ADMIN')")
    @Operation(summary = "Create a new equipment transaction", description = "Creates a new equipment issuance/return transaction with issued items, returned items, and checklist")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Transaction created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Staff or issuing officer not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<TransactionResponseDto>> createTransaction(
            @Valid @RequestBody CreateTransactionRequestDto request) {
        log.info("Creating new equipment transaction for staff ID: {}", request.getStaffId());
        TransactionResponseDto response = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Transaction created successfully", response));
    }

    @PostMapping("/issue")
    @PreAuthorize("hasAnyAuthority('ROLE_ICT_OFFICER', 'ROLE_ADMIN')")
    @Operation(summary = "Issue ICT equipment to staff", description = "Issues ICT equipment to a staff member with mandatory ICT checklist. Requires ICT_OFFICER or ADMIN role.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Equipment issued successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data or missing ICT checklist"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Staff, issuing officer, or equipment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<TransactionResponseDto>> issueEquipment(
            @Valid @RequestBody IssueEquipmentRequestDto request) {
        log.info("Issuing equipment to staff ID: {}", request.getStaffId());
        TransactionResponseDto response = transactionService.issueEquipment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Equipment issued successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID", description = "Retrieves a specific transaction by its ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transaction retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<ApiResponse<TransactionResponseDto>> getTransactionById(
            @Parameter(description = "Transaction ID") @PathVariable Long id) {
        log.info("Fetching transaction by ID: {}", id);
        TransactionResponseDto response = transactionService.getTransactionById(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Transaction retrieved successfully", response));
    }

    @GetMapping("/code/{transactionCode}")
    @Operation(summary = "Get transaction by code", description = "Retrieves a specific transaction by its transaction code")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transaction retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<ApiResponse<TransactionResponseDto>> getTransactionByCode(
            @Parameter(description = "Transaction code") @PathVariable String transactionCode) {
        log.info("Fetching transaction by code: {}", transactionCode);
        TransactionResponseDto response = transactionService.getTransactionByCode(transactionCode);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Transaction retrieved successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all transactions with filters", description = "Retrieves a paginated list of transactions with optional filters")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transactions retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Page<TransactionResponseDto>>> getTransactions(
            @Parameter(description = "Filter by staff ID") @RequestParam(required = false) Long staffId,
            @Parameter(description = "Filter by Transaction Code") @RequestParam(required = false) String transactionCode,
            @Parameter(description = "Filter by status (DRAFT, PENDING_SIGNATURE, COMPLETED, CANCELLED)") @RequestParam(required = false) String status,
            @Parameter(description = "Filter by start date") @RequestParam(required = false) String startDate,
            @Parameter(description = "Filter by end date") @RequestParam(required = false) String endDate,
            Pageable pageable) {
        log.info("Fetching transactions with filters - staffId: {}, status: {}, transactionCode:{}", staffId, status, transactionCode);

        TransactionFilterParams filters = TransactionFilterParams.builder()
                .staffId(staffId)
                .transactionCode(transactionCode)
                .status(status != null ? EquipmentTransaction.TransactionStatus.valueOf(status) : null)
                .startDate(null)
                .endDate(null)
                .build();

        Page<TransactionResponseDto> response = transactionService.getTransactions(filters, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Transactions retrieved successfully", response));
    }

    @PostMapping("/{id}/sign")
    @Operation(summary = "Submit signatures for transaction", description = "Submits employee and officer signatures to complete a transaction")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Signatures submitted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid signature data or transaction state"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<ApiResponse<TransactionResponseDto>> submitSignatures(
            @Parameter(description = "Transaction ID") @PathVariable Long id,
            @Valid @RequestBody SubmitSignatureRequestDto request) {
        log.info("Submitting signatures for transaction ID: {}", id);
        TransactionResponseDto response = transactionService.submitSignatures(id, request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Signatures submitted successfully", response));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a transaction", description = "Cancels a pending transaction and rolls back equipment statuses")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transaction cancelled successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Transaction cannot be cancelled in current state"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<ApiResponse<TransactionResponseDto>> cancelTransaction(
            @Parameter(description = "Transaction ID") @PathVariable Long id) {
        log.info("Cancelling transaction ID: {}", id);
        TransactionResponseDto response = transactionService.cancelTransaction(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Transaction cancelled successfully", response));
    }

    @GetMapping("/{id}/pdf")
    @Operation(summary = "Generate transaction PDF", description = "Generates a PDF document for the transaction with all details")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "PDF generated successfully", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/pdf")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<byte[]> generateTransactionPdf(
            @Parameter(description = "Transaction ID") @PathVariable Long id) {
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
