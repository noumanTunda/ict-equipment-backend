package com.tundalabs.ictequipment.service;

import com.tundalabs.ictequipment.dto.ReportFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReportService {

    // Equipment Reports
    Page<com.tundalabs.ictequipment.dto.EquipmentResponseDto> generateEquipmentReport(
            ReportFilterDto filter, 
            Pageable pageable
    );

    byte[] generateEquipmentReportCsv(ReportFilterDto filter);

    byte[] generateEquipmentReportPdf(ReportFilterDto filter);

    // Transaction Reports
    Page<com.tundalabs.ictequipment.dto.TransactionResponseDto> generateTransactionReport(
            ReportFilterDto filter,
            Pageable pageable
    );

    byte[] generateTransactionReportCsv(ReportFilterDto filter);

    byte[] generateTransactionReportPdf(ReportFilterDto filter);

    // Staff Asset Reports
    List<com.tundalabs.ictequipment.dto.AssetStatusResponseDto> generateStaffAssetReport(Long staffId);

    byte[] generateStaffAssetReportCsv(Long staffId);

    byte[] generateStaffAssetReportPdf(Long staffId);

    // Overdue Transaction Reports
    Page<com.tundalabs.ictequipment.dto.TransactionResponseDto> generateOverdueTransactionsReport(
            int overdueThresholdDays,
            Pageable pageable
    );

    byte[] generateOverdueTransactionsReportCsv(int overdueThresholdDays);

    byte[] generateOverdueTransactionsReportPdf(int overdueThresholdDays);
}
