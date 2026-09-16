package com.tundalabs.ictequipment.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.tundalabs.ictequipment.dto.*;
import com.tundalabs.ictequipment.entity.Equipment;
import com.tundalabs.ictequipment.entity.EquipmentTransaction;
import com.tundalabs.ictequipment.projection.AssetStatusProjection;
import com.tundalabs.ictequipment.repository.EquipmentRepository;
import com.tundalabs.ictequipment.repository.EquipmentTransactionRepository;
import com.tundalabs.ictequipment.repository.UserRepository;
import com.tundalabs.ictequipment.service.ReportService;
import com.tundalabs.ictequipment.specification.EquipmentSpecification;
import com.tundalabs.ictequipment.specification.TransactionSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentTransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Override
    public Page<EquipmentResponseDto> generateEquipmentReport(ReportFilterDto filter, Pageable pageable) {
        log.info("Generating equipment report with filters");
        var specification = EquipmentSpecification.buildFromFilter(filter);
        Page<Equipment> equipmentPage = equipmentRepository.findAll(specification, pageable);
        return equipmentPage.map(this::mapToEquipmentResponseDto);
    }

    @Override
    public byte[] generateEquipmentReportCsv(ReportFilterDto filter) {
        log.info("Generating equipment CSV report");
        var specification = EquipmentSpecification.buildFromFilter(filter);
        List<Equipment> equipmentList = equipmentRepository.findAll(specification);
        
        try (StringWriter stringWriter = new StringWriter();
             PrintWriter printWriter = new PrintWriter(stringWriter)) {
            
            printWriter.println("ID,Asset Number,Serial Number,Type,Department,Status,Brand Model,Supplier,Warranty,Warranty Months,Created At");
            
            for (Equipment equipment : equipmentList) {
                printWriter.printf("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    equipment.getId(),
                    equipment.getAssetNumber(),
                    equipment.getSerialNumber(),
                    equipment.getEquipmentType(),
                    equipment.getDepartment().name(),
                    equipment.getStatus().name(),
                    equipment.getBrandModel(),
                    equipment.getSupplierDetails(),
                    equipment.getHasWarranty(),
                    equipment.getWarrantyDurationMonths(),
                    equipment.getCreatedAt()
                );
            }
            
            return stringWriter.toString().getBytes();
        } catch (IOException e) {
            log.error("Error generating equipment CSV report", e);
            throw new RuntimeException("Failed to generate CSV report", e);
        }
    }

    @Override
    public byte[] generateEquipmentReportPdf(ReportFilterDto filter) {
        log.info("Generating equipment PDF report");
        var specification = EquipmentSpecification.buildFromFilter(filter);
        List<Equipment> equipmentList = equipmentRepository.findAll(specification);
        
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

            document.add(new Paragraph("PPRA ICT EQUIPMENT INVENTORY REPORT", titleFont));
            document.add(new Paragraph("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), normalFont));
            document.add(Chunk.NEWLINE);

            Table table = new Table(11);
            table.setWidths(new float[]{1f, 2f, 2f, 2f, 2f, 1.5f, 2f, 2f, 1f, 1.5f, 2f});

            table.addCell(new Cell(new Phrase("ID", headerFont)));
            table.addCell(new Cell(new Phrase("Asset Number", headerFont)));
            table.addCell(new Cell(new Phrase("Serial Number", headerFont)));
            table.addCell(new Cell(new Phrase("Type", headerFont)));
            table.addCell(new Cell(new Phrase("Department", headerFont)));
            table.addCell(new Cell(new Phrase("Status", headerFont)));
            table.addCell(new Cell(new Phrase("Brand Model", headerFont)));
            table.addCell(new Cell(new Phrase("Supplier", headerFont)));
            table.addCell(new Cell(new Phrase("Warranty", headerFont)));
            table.addCell(new Cell(new Phrase("Warranty Mo", headerFont)));
            table.addCell(new Cell(new Phrase("Created At", headerFont)));

            for (Equipment equipment : equipmentList) {
                table.addCell(new Cell(new Phrase(String.valueOf(equipment.getId()), normalFont)));
                table.addCell(new Cell(new Phrase(equipment.getAssetNumber(), normalFont)));
                table.addCell(new Cell(new Phrase(equipment.getSerialNumber(), normalFont)));
                table.addCell(new Cell(new Phrase(equipment.getEquipmentType(), normalFont)));
                table.addCell(new Cell(new Phrase(equipment.getDepartment().name(), normalFont)));
                table.addCell(new Cell(new Phrase(equipment.getStatus().name(), normalFont)));
                table.addCell(new Cell(new Phrase(equipment.getBrandModel(), normalFont)));
                table.addCell(new Cell(new Phrase(equipment.getSupplierDetails(), normalFont)));
                table.addCell(new Cell(new Phrase(equipment.getHasWarranty() != null ? equipment.getHasWarranty().toString() : "N/A", normalFont)));
                table.addCell(new Cell(new Phrase(equipment.getWarrantyDurationMonths() != null ? equipment.getWarrantyDurationMonths().toString() : "N/A", normalFont)));
                table.addCell(new Cell(new Phrase(equipment.getCreatedAt() != null ? equipment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "N/A", normalFont)));
            }

            document.add(table);
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Total Records: " + equipmentList.size(), normalFont));
            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating equipment PDF report", e);
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    @Override
    public Page<TransactionResponseDto> generateTransactionReport(ReportFilterDto filter, Pageable pageable) {
        log.info("Generating transaction report with filters");
        var specification = TransactionSpecification.buildFromFilter(filter);
        Page<EquipmentTransaction> transactionPage = transactionRepository.findAll(specification, pageable);
        return transactionPage.map(this::mapToTransactionResponseDto);
    }

    @Override
    public byte[] generateTransactionReportCsv(ReportFilterDto filter) {
        log.info("Generating transaction CSV report");
        var specification = TransactionSpecification.buildFromFilter(filter);
        List<EquipmentTransaction> transactions = transactionRepository.findAll(specification);
        
        try (StringWriter stringWriter = new StringWriter();
             PrintWriter printWriter = new PrintWriter(stringWriter)) {
            
            printWriter.println("Transaction Code,Staff ID,Officer ID,Status,Employee Signed,Officer Signed,Created At");
            
            for (EquipmentTransaction transaction : transactions) {
                printWriter.printf("%s,%d,%d,%s,%s,%s,%s%n",
                    transaction.getTransactionCode(),
                    transaction.getStaffId(),
                    transaction.getIssuingOfficerId(),
                    transaction.getStatus().name(),
                    transaction.getEmployeeSigned(),
                    transaction.getOfficerSigned(),
                    transaction.getCreatedAt()
                );
            }
            
            return stringWriter.toString().getBytes();
        } catch (IOException e) {
            log.error("Error generating transaction CSV report", e);
            throw new RuntimeException("Failed to generate CSV report", e);
        }
    }

    @Override
    public byte[] generateTransactionReportPdf(ReportFilterDto filter) {
        log.info("Generating transaction PDF report");
        var specification = TransactionSpecification.buildFromFilter(filter);
        List<EquipmentTransaction> transactions = transactionRepository.findAll(specification);
        
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            document.add(new Paragraph("PPRA ICT EQUIPMENT TRANSACTION REPORT", titleFont));
            document.add(new Paragraph("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), normalFont));
            document.add(Chunk.NEWLINE);

            Table table = new Table(7);
            table.setWidths(new float[]{3f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 2f});

            table.addCell(new Cell(new Phrase("Transaction Code", headerFont)));
            table.addCell(new Cell(new Phrase("Staff ID", headerFont)));
            table.addCell(new Cell(new Phrase("Officer ID", headerFont)));
            table.addCell(new Cell(new Phrase("Status", headerFont)));
            table.addCell(new Cell(new Phrase("Employee Signed", headerFont)));
            table.addCell(new Cell(new Phrase("Officer Signed", headerFont)));
            table.addCell(new Cell(new Phrase("Created At", headerFont)));

            for (EquipmentTransaction transaction : transactions) {
                table.addCell(new Cell(new Phrase(transaction.getTransactionCode(), normalFont)));
                table.addCell(new Cell(new Phrase(String.valueOf(transaction.getStaffId()), normalFont)));
                table.addCell(new Cell(new Phrase(String.valueOf(transaction.getIssuingOfficerId()), normalFont)));
                table.addCell(new Cell(new Phrase(transaction.getStatus().name(), normalFont)));
                table.addCell(new Cell(new Phrase(transaction.getEmployeeSigned() != null ? transaction.getEmployeeSigned().toString() : "N/A", normalFont)));
                table.addCell(new Cell(new Phrase(transaction.getOfficerSigned() != null ? transaction.getOfficerSigned().toString() : "N/A", normalFont)));
                table.addCell(new Cell(new Phrase(transaction.getCreatedAt() != null ? transaction.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A", normalFont)));
            }

            document.add(table);
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Total Records: " + transactions.size(), normalFont));
            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating transaction PDF report", e);
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    @Override
    public List<AssetStatusResponseDto> generateStaffAssetReport(Long staffId) {
        log.info("Generating staff asset report for staff ID: {}", staffId);
        List<AssetStatusProjection> projections = equipmentRepository.findStaffAssignedAssetsWithStatus(staffId);
        return projections.stream()
                .map(this::mapToAssetStatusResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] generateStaffAssetReportCsv(Long staffId) {
        log.info("Generating staff asset CSV report for staff ID: {}", staffId);
        List<AssetStatusResponseDto> assets = generateStaffAssetReport(staffId);
        
        try (StringWriter stringWriter = new StringWriter();
             PrintWriter printWriter = new PrintWriter(stringWriter)) {
            
            printWriter.println("Asset Number,Serial Number,Type,Status,Issued At,Returned At,Transaction Code");
            
            for (AssetStatusResponseDto asset : assets) {
                printWriter.printf("%s,%s,%s,%s,%s,%s,%s%n",
                    asset.getAssetNumber(),
                    asset.getSerialNumber(),
                    asset.getEquipmentType(),
                    asset.getStatus(),
                    asset.getIssuedAt(),
                    asset.getReturnedAt(),
                    asset.getTransactionCode()
                );
            }
            
            return stringWriter.toString().getBytes();
        } catch (IOException e) {
            log.error("Error generating staff asset CSV report", e);
            throw new RuntimeException("Failed to generate CSV report", e);
        }
    }

    @Override
    public byte[] generateStaffAssetReportPdf(Long staffId) {
        log.info("Generating staff asset PDF report for staff ID: {}", staffId);
        List<AssetStatusResponseDto> assets = generateStaffAssetReport(staffId);
        
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            document.add(new Paragraph("PPRA STAFF ASSET ASSIGNMENT REPORT", titleFont));
            document.add(new Paragraph("Staff ID: " + staffId, normalFont));
            document.add(new Paragraph("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), normalFont));
            document.add(Chunk.NEWLINE);

            Table table = new Table(7);
            table.setWidths(new float[]{2.5f, 2f, 1.5f, 1.5f, 2f, 2f, 2.5f});

            table.addCell(new Cell(new Phrase("Asset Number", headerFont)));
            table.addCell(new Cell(new Phrase("Serial Number", headerFont)));
            table.addCell(new Cell(new Phrase("Type", headerFont)));
            table.addCell(new Cell(new Phrase("Status", headerFont)));
            table.addCell(new Cell(new Phrase("Issued At", headerFont)));
            table.addCell(new Cell(new Phrase("Returned At", headerFont)));
            table.addCell(new Cell(new Phrase("Transaction Code", headerFont)));

            for (AssetStatusResponseDto asset : assets) {
                table.addCell(new Cell(new Phrase(asset.getAssetNumber(), normalFont)));
                table.addCell(new Cell(new Phrase(asset.getSerialNumber(), normalFont)));
                table.addCell(new Cell(new Phrase(asset.getEquipmentType(), normalFont)));
                table.addCell(new Cell(new Phrase(asset.getStatus().name(), normalFont)));
                table.addCell(new Cell(new Phrase(asset.getIssuedAt() != null ? asset.getIssuedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "N/A", normalFont)));
                table.addCell(new Cell(new Phrase(asset.getReturnedAt() != null ? asset.getReturnedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "N/A", normalFont)));
                table.addCell(new Cell(new Phrase(asset.getTransactionCode() != null ? asset.getTransactionCode() : "N/A", normalFont)));
            }

            document.add(table);
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Total Assets: " + assets.size(), normalFont));
            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating staff asset PDF report", e);
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    @Override
    public Page<TransactionResponseDto> generateOverdueTransactionsReport(int overdueThresholdDays, Pageable pageable) {
        log.info("Generating overdue transactions report with threshold: {} days", overdueThresholdDays);
        var specification = TransactionSpecification.isOverdue(overdueThresholdDays);
        Page<EquipmentTransaction> transactionPage = transactionRepository.findAll(specification, pageable);
        return transactionPage.map(this::mapToTransactionResponseDto);
    }

    @Override
    public byte[] generateOverdueTransactionsReportCsv(int overdueThresholdDays) {
        log.info("Generating overdue transactions CSV report with threshold: {} days", overdueThresholdDays);
        var specification = TransactionSpecification.isOverdue(overdueThresholdDays);
        List<EquipmentTransaction> transactions = transactionRepository.findAll(specification);
        
        try (StringWriter stringWriter = new StringWriter();
             PrintWriter printWriter = new PrintWriter(stringWriter)) {
            
            printWriter.println("Transaction Code,Staff ID,Officer ID,Status,Created At,Days Overdue");
            
            for (EquipmentTransaction transaction : transactions) {
                long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(
                    transaction.getCreatedAt(), LocalDateTime.now()
                );
                printWriter.printf("%s,%d,%d,%s,%s,%d%n",
                    transaction.getTransactionCode(),
                    transaction.getStaffId(),
                    transaction.getIssuingOfficerId(),
                    transaction.getStatus().name(),
                    transaction.getCreatedAt(),
                    daysOverdue
                );
            }
            
            return stringWriter.toString().getBytes();
        } catch (IOException e) {
            log.error("Error generating overdue transactions CSV report", e);
            throw new RuntimeException("Failed to generate CSV report", e);
        }
    }

    @Override
    public byte[] generateOverdueTransactionsReportPdf(int overdueThresholdDays) {
        log.info("Generating overdue transactions PDF report with threshold: {} days", overdueThresholdDays);
        var specification = TransactionSpecification.isOverdue(overdueThresholdDays);
        List<EquipmentTransaction> transactions = transactionRepository.findAll(specification);
        
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            document.add(new Paragraph("PPRA OVERDUE TRANSACTIONS REPORT", titleFont));
            document.add(new Paragraph("Overdue Threshold: " + overdueThresholdDays + " days", normalFont));
            document.add(new Paragraph("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), normalFont));
            document.add(Chunk.NEWLINE);

            Table table = new Table(6);
            table.setWidths(new float[]{3f, 1.5f, 1.5f, 1.5f, 2f, 1.5f});

            table.addCell(new Cell(new Phrase("Transaction Code", headerFont)));
            table.addCell(new Cell(new Phrase("Staff ID", headerFont)));
            table.addCell(new Cell(new Phrase("Officer ID", headerFont)));
            table.addCell(new Cell(new Phrase("Status", headerFont)));
            table.addCell(new Cell(new Phrase("Created At", headerFont)));
            table.addCell(new Cell(new Phrase("Days Overdue", headerFont)));

            for (EquipmentTransaction transaction : transactions) {
                long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(
                    transaction.getCreatedAt(), LocalDateTime.now()
                );
                table.addCell(new Cell(new Phrase(transaction.getTransactionCode(), normalFont)));
                table.addCell(new Cell(new Phrase(String.valueOf(transaction.getStaffId()), normalFont)));
                table.addCell(new Cell(new Phrase(String.valueOf(transaction.getIssuingOfficerId()), normalFont)));
                table.addCell(new Cell(new Phrase(transaction.getStatus().name(), normalFont)));
                table.addCell(new Cell(new Phrase(transaction.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), normalFont)));
                table.addCell(new Cell(new Phrase(String.valueOf(daysOverdue), normalFont)));
            }

            document.add(table);
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Total Overdue Transactions: " + transactions.size(), normalFont));
            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating overdue transactions PDF report", e);
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    private EquipmentResponseDto mapToEquipmentResponseDto(Equipment equipment) {
        return EquipmentResponseDto.builder()
                .id(equipment.getId())
                .assetNumber(equipment.getAssetNumber())
                .serialNumber(equipment.getSerialNumber())
                .equipmentType(equipment.getEquipmentType())
                .department(equipment.getDepartment())
                .brandModel(equipment.getBrandModel())
                .supplierDetails(equipment.getSupplierDetails())
                .status(equipment.getStatus())
                .description(equipment.getDescription())
                .hasWarranty(equipment.getHasWarranty())
                .warrantyDurationMonths(equipment.getWarrantyDurationMonths())
                .createdAt(equipment.getCreatedAt())
                .updatedAt(equipment.getUpdatedAt())
                .build();
    }

    private TransactionResponseDto mapToTransactionResponseDto(EquipmentTransaction transaction) {
        return TransactionResponseDto.builder()
                .id(transaction.getId())
                .transactionCode(transaction.getTransactionCode())
                .staffId(transaction.getStaffId())
                .issuingOfficerId(transaction.getIssuingOfficerId())
                .status(transaction.getStatus().name())
                .employeeSigned(transaction.getEmployeeSigned() != null ? transaction.getEmployeeSigned() : false)
                .officerSigned(transaction.getOfficerSigned() != null ? transaction.getOfficerSigned() : false)
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }

    private AssetStatusResponseDto mapToAssetStatusResponseDto(AssetStatusProjection projection) {
        return AssetStatusResponseDto.builder()
                .id(projection.getId())
                .assetNumber(projection.getAssetNumber())
                .serialNumber(projection.getSerialNumber())
                .equipmentType(projection.getEquipmentType())
                .status(projection.getStatus())
                .issuedAt(projection.getIssuedAt())
                .returnedAt(projection.getReturnedAt())
                .transactionStatus(projection.getTransactionStatus())
                .transactionCode(projection.getTransactionCode())
                .staffId(projection.getStaffId())
                .build();
    }
}
