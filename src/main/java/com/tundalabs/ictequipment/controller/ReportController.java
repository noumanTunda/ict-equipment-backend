package com.tundalabs.ictequipment.controller;

import com.tundalabs.ictequipment.dto.ApiResponse;
import com.tundalabs.ictequipment.dto.ReportFilterDto;
import com.tundalabs.ictequipment.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reporting System", description = "APIs for generating dynamic reports in multiple formats")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;

    // Equipment Reports
    @GetMapping("/equipment")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ICT_OFFICER')")
    @Operation(summary = "Generate equipment report (JSON)", description = "Returns paginated equipment report in JSON format. Requires ADMIN or ICT_OFFICER role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Page<com.tundalabs.ictequipment.dto.EquipmentResponseDto>>> generateEquipmentReportJson(
            @Valid ReportFilterDto filter,
            Pageable pageable) {
        log.info("Generating equipment JSON report");
        Page<com.tundalabs.ictequipment.dto.EquipmentResponseDto> response = reportService.generateEquipmentReport(filter, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Equipment report generated successfully", response));
    }

    @GetMapping(value = "/equipment/csv", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ICT_OFFICER')")
    @Operation(summary = "Generate equipment report (CSV)", description = "Returns equipment report in CSV format. Requires ADMIN or ICT_OFFICER role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "CSV report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<byte[]> generateEquipmentReportCsv(@Valid ReportFilterDto filter) {
        log.info("Generating equipment CSV report");
        byte[] csvData = reportService.generateEquipmentReportCsv(filter);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "equipment_report.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }

    @GetMapping(value = "/equipment/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ICT_OFFICER')")
    @Operation(summary = "Generate equipment report (PDF)", description = "Returns equipment report in PDF format. Requires ADMIN or ICT_OFFICER role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "PDF report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<byte[]> generateEquipmentReportPdf(@Valid ReportFilterDto filter) {
        log.info("Generating equipment PDF report");
        byte[] pdfData = reportService.generateEquipmentReportPdf(filter);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "equipment_report.pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfData);
    }

    // Transaction Reports
    @GetMapping("/transactions")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ICT_OFFICER')")
    @Operation(summary = "Generate transaction report (JSON)", description = "Returns paginated transaction report in JSON format. Requires ADMIN or ICT_OFFICER role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Page<com.tundalabs.ictequipment.dto.TransactionResponseDto>>> generateTransactionReportJson(
            @Valid ReportFilterDto filter,
            Pageable pageable) {
        log.info("Generating transaction JSON report");
        Page<com.tundalabs.ictequipment.dto.TransactionResponseDto> response = reportService.generateTransactionReport(filter, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Transaction report generated successfully", response));
    }

    @GetMapping(value = "/transactions/csv", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ICT_OFFICER')")
    @Operation(summary = "Generate transaction report (CSV)", description = "Returns transaction report in CSV format. Requires ADMIN or ICT_OFFICER role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "CSV report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<byte[]> generateTransactionReportCsv(@Valid ReportFilterDto filter) {
        log.info("Generating transaction CSV report");
        byte[] csvData = reportService.generateTransactionReportCsv(filter);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "transaction_report.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }

    @GetMapping(value = "/transactions/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ICT_OFFICER')")
    @Operation(summary = "Generate transaction report (PDF)", description = "Returns transaction report in PDF format. Requires ADMIN or ICT_OFFICER role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "PDF report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<byte[]> generateTransactionReportPdf(@Valid ReportFilterDto filter) {
        log.info("Generating transaction PDF report");
        byte[] pdfData = reportService.generateTransactionReportPdf(filter);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "transaction_report.pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfData);
    }

    // Staff Asset Reports
    @GetMapping("/staff/{staffId}/assets")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ICT_OFFICER')")
    @Operation(summary = "Generate staff asset report (JSON)", description = "Returns staff asset assignment report in JSON format. Requires ADMIN or ICT_OFFICER role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Staff not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<java.util.List<com.tundalabs.ictequipment.dto.AssetStatusResponseDto>>> generateStaffAssetReportJson(
            @Parameter(description = "Staff ID") @PathVariable Long staffId) {
        log.info("Generating staff asset JSON report for staff ID: {}", staffId);
        java.util.List<com.tundalabs.ictequipment.dto.AssetStatusResponseDto> response = reportService.generateStaffAssetReport(staffId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Staff asset report generated successfully", response));
    }

    @GetMapping(value = "/staff/{staffId}/assets/csv", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ICT_OFFICER')")
    @Operation(summary = "Generate staff asset report (CSV)", description = "Returns staff asset assignment report in CSV format. Requires ADMIN or ICT_OFFICER role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "CSV report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Staff not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<byte[]> generateStaffAssetReportCsv(
            @Parameter(description = "Staff ID") @PathVariable Long staffId) {
        log.info("Generating staff asset CSV report for staff ID: {}", staffId);
        byte[] csvData = reportService.generateStaffAssetReportCsv(staffId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "staff_asset_report_" + staffId + ".csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }

    @GetMapping(value = "/staff/{staffId}/assets/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ICT_OFFICER')")
    @Operation(summary = "Generate staff asset report (PDF)", description = "Returns staff asset assignment report in PDF format. Requires ADMIN or ICT_OFFICER role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "PDF report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Staff not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<byte[]> generateStaffAssetReportPdf(
            @Parameter(description = "Staff ID") @PathVariable Long staffId) {
        log.info("Generating staff asset PDF report for staff ID: {}", staffId);
        byte[] pdfData = reportService.generateStaffAssetReportPdf(staffId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "staff_asset_report_" + staffId + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfData);
    }

    // Overdue Transaction Reports
    @GetMapping("/transactions/overdue")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ICT_OFFICER')")
    @Operation(summary = "Generate overdue transactions report (JSON)", description = "Returns paginated overdue transactions report in JSON format. Requires ADMIN or ICT_OFFICER role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Page<com.tundalabs.ictequipment.dto.TransactionResponseDto>>> generateOverdueTransactionsReportJson(
            @Parameter(description = "Overdue threshold in days") @RequestParam(defaultValue = "30") int overdueThresholdDays,
            Pageable pageable) {
        log.info("Generating overdue transactions JSON report with threshold: {} days", overdueThresholdDays);
        Page<com.tundalabs.ictequipment.dto.TransactionResponseDto> response = reportService.generateOverdueTransactionsReport(overdueThresholdDays, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Overdue transactions report generated successfully", response));
    }

    @GetMapping(value = "/transactions/overdue/csv", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ICT_OFFICER')")
    @Operation(summary = "Generate overdue transactions report (CSV)", description = "Returns overdue transactions report in CSV format. Requires ADMIN or ICT_OFFICER role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "CSV report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<byte[]> generateOverdueTransactionsReportCsv(
            @Parameter(description = "Overdue threshold in days") @RequestParam(defaultValue = "30") int overdueThresholdDays) {
        log.info("Generating overdue transactions CSV report with threshold: {} days", overdueThresholdDays);
        byte[] csvData = reportService.generateOverdueTransactionsReportCsv(overdueThresholdDays);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "overdue_transactions_report.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }

    @GetMapping(value = "/transactions/overdue/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ICT_OFFICER')")
    @Operation(summary = "Generate overdue transactions report (PDF)", description = "Returns overdue transactions report in PDF format. Requires ADMIN or ICT_OFFICER role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "PDF report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<byte[]> generateOverdueTransactionsReportPdf(
            @Parameter(description = "Overdue threshold in days") @RequestParam(defaultValue = "30") int overdueThresholdDays) {
        log.info("Generating overdue transactions PDF report with threshold: {} days", overdueThresholdDays);
        byte[] pdfData = reportService.generateOverdueTransactionsReportPdf(overdueThresholdDays);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "overdue_transactions_report.pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfData);
    }
}
