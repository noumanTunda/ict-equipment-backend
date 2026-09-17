package com.tundalabs.ictequipment.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.tundalabs.ictequipment.dto.*;
import com.tundalabs.ictequipment.entity.*;
import com.tundalabs.ictequipment.exception.EquipmentUnavailableException;
import com.tundalabs.ictequipment.exception.InvalidTransactionStateException;
import com.tundalabs.ictequipment.repository.*;
import com.tundalabs.ictequipment.service.EquipmentTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EquipmentTransactionServiceImpl implements EquipmentTransactionService {

    private final EquipmentTransactionRepository transactionRepository;
    private final EquipmentRepository equipmentRepository;
    private final TransactionIssuedItemRepository issuedItemRepository;
    private final TransactionReturnedItemRepository returnedItemRepository;
    private final IctChecklistRepository checklistRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public TransactionResponseDto createTransaction(CreateTransactionRequestDto request) {
        log.info("Creating transaction for staff ID: {}", request.getStaffId());

        // Validate staff and issuing officer exist
        User staff = userRepository.findById(request.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff member not found with ID: " + request.getStaffId()));

        User officer = userRepository.findById(request.getIssuingOfficerId())
                .orElseThrow(() -> new RuntimeException("Issuing officer not found with ID: " + request.getIssuingOfficerId()));

        // Generate unique transaction code
        String transactionCode = generateTransactionCode();

        // Create transaction
        EquipmentTransaction transaction = EquipmentTransaction.builder()
                .transactionCode(transactionCode)
                .staffId(request.getStaffId())
                .issuingOfficerId(request.getIssuingOfficerId())
                .status(EquipmentTransaction.TransactionStatus.PENDING_SIGNATURE)
                .build();

        transaction = transactionRepository.save(transaction);

        // Process issued items (Part B)
        List<TransactionIssuedItem> issuedItems = processIssuedItems(request.getIssuedItems(), transaction);
        transaction.setIssuedItems(issuedItems);

        // Process returned items (Part C) if provided
        if (request.getReturnedItems() != null && !request.getReturnedItems().isEmpty()) {
            Set<TransactionReturnedItem> returnedItems = new HashSet<>(processReturnedItems(request.getReturnedItems(), transaction));
            transaction.setReturnedItems(returnedItems);
        }

        // Process checklist (Part E) if provided
        if (request.getChecklist() != null) {
            IctChecklist checklist = processChecklist(request.getChecklist(), transaction);
            transaction.setChecklist(checklist);
        }

        transaction = transactionRepository.save(transaction);
        log.info("Transaction created successfully with code: {}", transactionCode);

        return mapToResponseDto(transaction, staff.getFullName(), officer.getFullName());
    }

    @Override
    @Transactional
    public TransactionResponseDto issueEquipment(IssueEquipmentRequestDto request) {
        log.info("Issuing equipment to staff ID: {}", request.getStaffId());

        // Validate staff and issuing officer exist
        User staff = userRepository.findById(request.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff member not found with ID: " + request.getStaffId()));

        User officer = userRepository.findById(request.getIssuingOfficerId())
                .orElseThrow(() -> new RuntimeException("Issuing officer not found with ID: " + request.getIssuingOfficerId()));

        // Generate unique transaction code
        String transactionCode = generateTransactionCode();

        // Create transaction for equipment issuance
        EquipmentTransaction transaction = EquipmentTransaction.builder()
                .transactionCode(transactionCode)
                .staffId(request.getStaffId())
                .issuingOfficerId(request.getIssuingOfficerId())
                .status(EquipmentTransaction.TransactionStatus.PENDING_SIGNATURE)
                .build();

        transaction = transactionRepository.save(transaction);

        // Process issued items (Part B)
        List<TransactionIssuedItem> issuedItems = processIssuedItems(request.getIssuedItems(), transaction);
        transaction.setIssuedItems(issuedItems);

        // Process ICT checklist (Part E) - mandatory for equipment issuance
        if (request.getChecklist() != null) {
            IctChecklist checklist = processChecklist(request.getChecklist(), transaction);
            transaction.setChecklist(checklist);
        } else {
            throw new IllegalArgumentException("ICT checklist is required for equipment issuance");
        }

        transaction = transactionRepository.save(transaction);
        log.info("Equipment issued successfully with transaction code: {}", transactionCode);

        return mapToResponseDto(transaction, staff.getFullName(), officer.getFullName());
    }

    @Override
    public TransactionResponseDto getTransactionById(Long id) {
        EquipmentTransaction transaction = transactionRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + id));

        User staff = userRepository.findById(transaction.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff member not found"));
        User officer = userRepository.findById(transaction.getIssuingOfficerId())
                .orElseThrow(() -> new RuntimeException("Issuing officer not found"));

        return mapToResponseDto(transaction, staff.getFullName(), officer.getFullName());
    }

    @Override
    public TransactionResponseDto getTransactionByCode(String transactionCode) {
        EquipmentTransaction transaction = transactionRepository.findByTransactionCode(transactionCode)
                .orElseThrow(() -> new RuntimeException("Transaction not found with code: " + transactionCode));

        User staff = userRepository.findById(transaction.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff member not found"));
        User officer = userRepository.findById(transaction.getIssuingOfficerId())
                .orElseThrow(() -> new RuntimeException("Issuing officer not found"));

        return mapToResponseDto(transaction, staff.getFullName(), officer.getFullName());
    }

    @Override
    public Page<TransactionResponseDto> getTransactions(TransactionFilterParams filters, Pageable pageable) {
        Page<EquipmentTransaction> transactions = transactionRepository.findByFilters(
                filters.getStaffId(),
                filters.getStatus(),
                filters.getStartDate(),
                filters.getEndDate(),
                pageable
        );

        // Collect all staff IDs and officer IDs
        List<Long> staffIds = transactions.getContent().stream().map(EquipmentTransaction::getStaffId).distinct().toList();
        List<Long> officerIds = transactions.getContent().stream().map(EquipmentTransaction::getIssuingOfficerId).distinct().toList();
        
        // Batch fetch all users
        List<User> staffUsers = userRepository.findAllById(staffIds);
        List<User> officerUsers = userRepository.findAllById(officerIds);
        
        // Create maps for quick lookup
        java.util.Map<Long, String> staffNameMap = staffUsers.stream()
                .collect(java.util.stream.Collectors.toMap(User::getId, User::getFullName));
        java.util.Map<Long, String> officerNameMap = officerUsers.stream()
                .collect(java.util.stream.Collectors.toMap(User::getId, User::getFullName));

        return transactions.map(transaction -> {
            String staffName = staffNameMap.get(transaction.getStaffId());
            String officerName = officerNameMap.get(transaction.getIssuingOfficerId());
            return mapToResponseDto(transaction, staffName, officerName);
        });
    }

    @Override
    @Transactional
    public TransactionResponseDto signTransactionAsEmployee(Long transactionId, SignTransactionDto request) {
        log.info("Employee signing transaction ID: {}", transactionId);

        EquipmentTransaction transaction = transactionRepository.findByIdWithDetails(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));

        // Validate transaction is in PENDING_SIGNATURE status
        if (transaction.getStatus() != EquipmentTransaction.TransactionStatus.PENDING_SIGNATURE) {
            throw new InvalidTransactionStateException(
                    "Transaction must be in PENDING_SIGNATURE status to sign. Current status: " + transaction.getStatus()
            );
        }

        // Check if employee has already signed
        if (transaction.getEmployeeSigned() != null && transaction.getEmployeeSigned()) {
            throw new InvalidTransactionStateException("Employee has already signed this transaction");
        }

        // Get the staff user and validate keyphrase
        User staff = userRepository.findById(transaction.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff member not found"));

        if (staff.getKeyphrase() == null) {
            throw new InvalidTransactionStateException("No keyphrase found for your Account. Please set a keyphrase first.");
        }

        // Validate the provided keyphrase against the stored hash
        if (!passwordEncoder.matches(request.getKeyphrase(), staff.getKeyphrase())) {
            throw new InvalidTransactionStateException("Invalid keyphrase. Please check your keyphrase and try again.");
        }

        // Mark as signed
        transaction.setEmployeeSigned(true);
        transaction = transactionRepository.save(transaction);

        log.info("Employee signed transaction ID: {} successfully", transactionId);

        // Check if both parties have signed - if so, complete the transaction
        if (Boolean.TRUE.equals(transaction.getEmployeeSigned()) && Boolean.TRUE.equals(transaction.getOfficerSigned())) {
            completeTransaction(transaction);
        }

        User officer = userRepository.findById(transaction.getIssuingOfficerId())
                .orElseThrow(() -> new RuntimeException("Issuing officer not found"));

        return mapToResponseDto(transaction, staff.getFullName(), officer.getFullName());
    }

    @Override
    @Transactional
    public TransactionResponseDto signTransactionAsOfficer(Long transactionId, SignTransactionDto request) {
        log.info("Officer signing transaction ID: {}", transactionId);

        EquipmentTransaction transaction = transactionRepository.findByIdWithDetails(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));

        // Validate transaction is in PENDING_SIGNATURE status
        if (transaction.getStatus() != EquipmentTransaction.TransactionStatus.PENDING_SIGNATURE) {
            throw new InvalidTransactionStateException(
                    "Transaction must be in PENDING_SIGNATURE status to sign. Current status: " + transaction.getStatus()
            );
        }

        // Check if officer has already signed
        if (transaction.getOfficerSigned() != null && transaction.getOfficerSigned()) {
            throw new InvalidTransactionStateException("Officer has already signed this transaction");
        }

        // Get the officer user and validate keyphrase
        User officer = userRepository.findById(transaction.getIssuingOfficerId())
                .orElseThrow(() -> new RuntimeException("Issuing officer not found"));

        if (officer.getKeyphrase() == null) {
            throw new InvalidTransactionStateException("Officer has not set a keyphrase. Please set a keyphrase first.");
        }

        // Validate the provided keyphrase against the stored hash
        if (!passwordEncoder.matches(request.getKeyphrase(), officer.getKeyphrase())) {
            throw new InvalidTransactionStateException("Invalid keyphrase. Please check your keyphrase and try again.");
        }

        // Mark as signed
        transaction.setOfficerSigned(true);
        transaction = transactionRepository.save(transaction);

        log.info("Officer signed transaction ID: {} successfully", transactionId);

        // Check if both parties have signed - if so, complete the transaction
        if (Boolean.TRUE.equals(transaction.getEmployeeSigned()) && Boolean.TRUE.equals(transaction.getOfficerSigned())) {
            completeTransaction(transaction);
        }

        User staff = userRepository.findById(transaction.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff member not found"));

        return mapToResponseDto(transaction, staff.getFullName(), officer.getFullName());
    }

    private void completeTransaction(EquipmentTransaction transaction) {
        log.info("Completing transaction ID: {}", transaction.getId());

        // Update equipment statuses
        if (transaction.getIssuedItems() != null) {
            transaction.getIssuedItems().forEach(issuedItem -> {
                Equipment equipment = issuedItem.getEquipment();
                if (equipment.getStatus() == Equipment.EquipmentStatus.AVAILABLE) {
                    equipment.setStatus(Equipment.EquipmentStatus.ISSUED);
                    equipmentRepository.save(equipment);
                    log.info("Updated equipment {} from AVAILABLE to ISSUED", equipment.getAssetNumber());
                }
            });
        }

        if (transaction.getReturnedItems() != null) {
            transaction.getReturnedItems().forEach(returnedItem -> {
                Equipment equipment = returnedItem.getEquipment();
                if (equipment.getStatus() == Equipment.EquipmentStatus.ISSUED) {
                    equipment.setStatus(Equipment.EquipmentStatus.RETURNED);
                    equipmentRepository.save(equipment);
                    log.info("Updated equipment {} from ISSUED to RETURNED", equipment.getAssetNumber());
                }
            });
        }

        transaction.setStatus(EquipmentTransaction.TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);
        log.info("Transaction completed successfully with ID: {}", transaction.getId());
    }

    @Override
    @Transactional
    public TransactionResponseDto cancelTransaction(Long transactionId) {
        log.info("Cancelling transaction ID: {}", transactionId);

        EquipmentTransaction transaction = transactionRepository.findByIdWithDetails(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));

        // Validate transaction is in PENDING_SIGNATURE status
        if (transaction.getStatus() != EquipmentTransaction.TransactionStatus.PENDING_SIGNATURE) {
            throw new InvalidTransactionStateException(
                    "Only PENDING_SIGNATURE transactions can be cancelled. Current status: " + transaction.getStatus()
            );
        }

        // Rollback equipment statuses
        rollbackEquipmentStatuses(transaction);

        // Update transaction status
        transaction.setStatus(EquipmentTransaction.TransactionStatus.CANCELLED);
        transaction = transactionRepository.save(transaction);

        log.info("Transaction cancelled successfully with ID: {}", transactionId);

        User staff = userRepository.findById(transaction.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff member not found"));
        User officer = userRepository.findById(transaction.getIssuingOfficerId())
                .orElseThrow(() -> new RuntimeException("Issuing officer not found"));

        return mapToResponseDto(transaction, staff.getFullName(), officer.getFullName());
    }

    @Override
    public byte[] generateTransactionPdf(Long transactionId) {
        EquipmentTransaction transaction = transactionRepository.findByIdWithDetails(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));

        User staff = userRepository.findById(transaction.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff member not found"));
        User officer = userRepository.findById(transaction.getIssuingOfficerId())
                .orElseThrow(() -> new RuntimeException("Issuing officer not found"));

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Expanded Top/Bottom margins (50pt) to accommodate header & footer rules
            Document document = new Document(PageSize.A4, 36, 36, 50, 50);
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            // Attach Header/Footer Event Listener
            writer.setPageEvent(new PdfHeaderFooterEvent());

            document.open();

            // Color Palette
            Color primaryColor = new Color(19, 148, 219);     // #1394db
            Color darkSlate = new Color(30, 41, 59);          // #1e293b
            Color lightBg = new Color(248, 250, 252);         // #f8fafc
            Color borderGray = new Color(226, 232, 240);      // #e2e8f0
            Color headerBg = new Color(241, 245, 249);        // #f1f5f9
            Color greenStatus = new Color(16, 185, 129);      // Green

            // Typography
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, primaryColor);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, primaryColor);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, darkSlate);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 8, darkSlate);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, darkSlate);
            Font signedFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, greenStatus);

            // --- HEADER BANNER ---
            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);
            PdfPCell headerCell = new PdfPCell();
            headerCell.setPadding(10);
            headerCell.setBackgroundColor(lightBg);
            headerCell.setBorderColor(borderGray);
            headerCell.setBorderWidth(1f);

            Paragraph pTitle = new Paragraph("EQUIPMENT ISSUE AND RETURN FORM", titleFont);
            pTitle.setAlignment(Element.ALIGN_CENTER);
            headerCell.addElement(pTitle);

            Paragraph pSub = new Paragraph("ICT EQUIPMENT MANAGEMENT SYSTEM", subTitleFont);
            pSub.setAlignment(Element.ALIGN_CENTER);
            headerCell.addElement(pSub);

            headerTable.addCell(headerCell);
            document.add(headerTable);

            // Helper Lambda for Section Headers
            java.util.function.Consumer<String> addSectionHeader = (title) -> {
                try {
                    PdfPTable secTable = new PdfPTable(1);
                    secTable.setWidthPercentage(100);
                    secTable.setSpacingBefore(8f);
                    secTable.setSpacingAfter(4f);
                    PdfPCell cell = new PdfPCell(new Phrase(title, sectionFont));
                    cell.setBackgroundColor(headerBg);
                    cell.setBorderColor(borderGray);
                    cell.setPadding(5f);
                    secTable.addCell(cell);
                    document.add(secTable);
                } catch (Exception e) {
                    log.error("Failed to render section header", e);
                }
            };

            // --- PART A: TRANSACTION DETAILS ---
            addSectionHeader.accept("PART A - TRANSACTION DETAILS");

            PdfPTable partA = new PdfPTable(6);
            partA.setWidthPercentage(100);
            partA.setWidths(new float[]{1.8f, 3.2f, 0.6f, 1.6f, 0.8f, 2.2f});

            addKeyValuePair(partA, "Transaction Code:", transaction.getTransactionCode(), boldFont, normalFont, borderGray);
            addKeyValuePair(partA, "Date:", transaction.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), boldFont, normalFont, borderGray);
            addKeyValuePair(partA, "Status:", transaction.getStatus().toString(), boldFont, normalFont, borderGray);

            document.add(partA);

            // --- PART B & C: PERSONNEL INFORMATION ---
            addSectionHeader.accept("PART B & C - PERSONNEL INFORMATION");
            PdfPTable partyTable = new PdfPTable(2);
            partyTable.setWidthPercentage(100);
            partyTable.setWidths(new float[]{1f, 1f});

            PdfPCell staffCell = new PdfPCell();
            staffCell.setPadding(6f);
            staffCell.setBorderColor(borderGray);
            staffCell.addElement(new Paragraph("STAFF DETAILS", boldFont));
            staffCell.addElement(new Paragraph("Name: " + staff.getFullName(), normalFont));
            staffCell.addElement(new Paragraph("Department: " + (staff.getDepartment() != null ? staff.getDepartment() : "N/A"), normalFont));
            staffCell.addElement(new Paragraph("Email: " + staff.getEmail(), normalFont));
            partyTable.addCell(staffCell);

            PdfPCell officerCell = new PdfPCell();
            officerCell.setPadding(6f);
            officerCell.setBorderColor(borderGray);
            officerCell.addElement(new Paragraph("ISSUING OFFICER DETAILS", boldFont));
            officerCell.addElement(new Paragraph("Name: " + officer.getFullName(), normalFont));
            officerCell.addElement(new Paragraph("Department: " + (officer.getDepartment() != null ? officer.getDepartment() : "N/A"), normalFont));
            officerCell.addElement(new Paragraph("Email: " + officer.getEmail(), normalFont));
            partyTable.addCell(officerCell);

            document.add(partyTable);

            // --- PART D: ISSUED ITEMS ---
            addSectionHeader.accept("PART D - ISSUED ITEMS");
            if (transaction.getIssuedItems() != null && !transaction.getIssuedItems().isEmpty()) {
                PdfPTable issuedTable = new PdfPTable(4);
                issuedTable.setWidthPercentage(100);
                issuedTable.setWidths(new float[]{1.4f, 1.4f, 4f, 4f});

                addTableHeader(issuedTable, "Asset Number", headerFont, headerBg, borderGray);
                addTableHeader(issuedTable, "Serial Number", headerFont, headerBg, borderGray);
                addTableHeader(issuedTable, "Equipment Name", headerFont, headerBg, borderGray);
                addTableHeader(issuedTable, "Accessories Provided", headerFont, headerBg, borderGray);

                for (TransactionIssuedItem item : transaction.getIssuedItems()) {
                    addTableCell(issuedTable, item.getEquipment().getAssetNumber(), normalFont, borderGray);
                    addTableCell(issuedTable, item.getEquipment().getSerialNumber(), normalFont, borderGray);
                    addTableCell(issuedTable, item.getEquipment().getBrandModel(), normalFont, borderGray);
                    addTableCell(issuedTable, item.getAccessoriesProvided() != null ? item.getAccessoriesProvided() : "N/A", normalFont, borderGray);
                }
                document.add(issuedTable);
            } else {
                document.add(new Paragraph("No items issued in this transaction.", normalFont));
            }

            // --- PART E: RETURNED ITEMS ---
            addSectionHeader.accept("PART E - RETURNED ITEMS");
            if (transaction.getReturnedItems() != null && !transaction.getReturnedItems().isEmpty()) {
                PdfPTable returnedTable = new PdfPTable(4);
                returnedTable.setWidthPercentage(100);
                returnedTable.setWidths(new float[]{1.4f, 3f, 2f, 4f});

                addTableHeader(returnedTable, "Asset Number", headerFont, headerBg, borderGray);
                addTableHeader(returnedTable, "Equipment Name", headerFont, headerBg, borderGray);
                addTableHeader(returnedTable, "Condition", headerFont, headerBg, borderGray);
                addTableHeader(returnedTable, "Remarks", headerFont, headerBg, borderGray);

                for (TransactionReturnedItem item : transaction.getReturnedItems()) {
                    addTableCell(returnedTable, item.getEquipment().getAssetNumber(), normalFont, borderGray);
                    addTableCell(returnedTable, item.getEquipment().getBrandModel(), normalFont, borderGray);
                    addTableCell(returnedTable, item.getItemCondition().name(), normalFont, borderGray);
                    addTableCell(returnedTable, item.getRemarks() != null ? item.getRemarks() : "N/A", normalFont, borderGray);
                }
                document.add(returnedTable);
            } else {
                document.add(new Paragraph("No items returned in this transaction.", normalFont));
            }

            // --- PART F: ICT CHECKLIST ---
            addSectionHeader.accept("PART F - MANDATORY ICT CHECKLIST");
            if (transaction.getChecklist() != null) {
                IctChecklist chk = transaction.getChecklist();
                PdfPTable chkTable = new PdfPTable(4);
                chkTable.setWidthPercentage(100);
                chkTable.setWidths(new float[]{1.4f, 3f, 1.4f, 3f});

                addKeyValuePair(chkTable, "OS Installed:", chk.getOsInstalled() != null ? chk.getOsInstalled() : "N/A", boldFont, normalFont, borderGray);
                addKeyValuePair(chkTable, "Apps Installed:", chk.getAppSystemInstalled() != null ? chk.getAppSystemInstalled() : "N/A", boldFont, normalFont, borderGray);
                addKeyValuePair(chkTable, "Antivirus:", chk.getAntiVirusInstalled() != null ? chk.getAntiVirusInstalled() : "N/A", boldFont, normalFont, borderGray);
                addKeyValuePair(chkTable, "PDF Reader:", chk.getPdfReaderInstalled() != null ? chk.getPdfReaderInstalled() : "N/A", boldFont, normalFont, borderGray);
                addKeyValuePair(chkTable, "Joined Domain:", Boolean.TRUE.equals(chk.getIsJoinedToDomain()) ? "Yes" : "No", boldFont, normalFont, borderGray);
                addKeyValuePair(chkTable, "VPN Configured:", Boolean.TRUE.equals(chk.getIsInstalledVpn()) ? "Yes" : "No", boldFont, normalFont, borderGray);
                addKeyValuePair(chkTable, "Printer Configured:", Boolean.TRUE.equals(chk.getIsInstalledPrinter()) ? "Yes" : "No", boldFont, normalFont, borderGray);
                addKeyValuePair(chkTable, "Additional Notes:", chk.getAdditionalNotes() != null ? chk.getAdditionalNotes() : "N/A", boldFont, normalFont, borderGray);

                document.add(chkTable);
            } else {
                document.add(new Paragraph("Checklist not required or not recorded.", normalFont));
            }

            // --- PART G: DIGITAL SIGNATURES ---
            addSectionHeader.accept("PART G - DIGITAL SIGNATURE ACKNOWLEDGEMENT");
            PdfPTable sigTable = new PdfPTable(2);
            sigTable.setWidthPercentage(100);
            sigTable.setWidths(new float[]{1f, 1f});

            boolean staffIsSigned = Boolean.TRUE.equals(transaction.getEmployeeSigned());
            PdfPCell staffSigCell = new PdfPCell();
            staffSigCell.setPadding(8f);
            staffSigCell.setBorderColor(borderGray);
            staffSigCell.addElement(new Paragraph("Staff Member Verification", boldFont));
            staffSigCell.addElement(new Paragraph("Name: " + staff.getFullName(), normalFont));
            staffSigCell.addElement(new Paragraph("Status: " + (staffIsSigned ? "SIGNED (Keyphrase Verified)" : "Pending Signature"), staffIsSigned ? signedFont : normalFont));
            sigTable.addCell(staffSigCell);

            boolean officerIsSigned = Boolean.TRUE.equals(transaction.getOfficerSigned());
            PdfPCell officerSigCell = new PdfPCell();
            officerSigCell.setPadding(8f);
            officerSigCell.setBorderColor(borderGray);
            officerSigCell.addElement(new Paragraph("Issuing Officer Verification", boldFont));
            officerSigCell.addElement(new Paragraph("Name: " + officer.getFullName(), normalFont));
            officerSigCell.addElement(new Paragraph("Status: " + (officerIsSigned ? "SIGNED (Keyphrase Verified)" : "Pending Signature"), officerIsSigned ? signedFont : normalFont));
            sigTable.addCell(officerSigCell);

            document.add(sigTable);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF for transaction ID: {}", transactionId, e);
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }

// --- HELPER METHODS FOR CLEAN TABLE BUILDING ---

    private void addTableHeader(PdfPTable table, String text, Font font, Color bgColor, Color borderColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setBorderColor(borderColor);
        cell.setPadding(5f);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Font font, Color borderColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorderColor(borderColor);
        cell.setPadding(5f);
        table.addCell(cell);
    }

    private void addKeyValuePair(PdfPTable table, String key, String value, Font keyFont, Font valueFont, Color borderColor) {
        PdfPCell keyCell = new PdfPCell(new Phrase(key, keyFont));
        keyCell.setBorderColor(borderColor);
        keyCell.setPadding(4f);
        keyCell.setBackgroundColor(new Color(250, 250, 250));
        table.addCell(keyCell);

        PdfPCell valCell = new PdfPCell(new Phrase(value, valueFont));
        valCell.setBorderColor(borderColor);
        valCell.setPadding(4f);
        table.addCell(valCell);
    }

    private List<TransactionIssuedItem> processIssuedItems(List<IssuedItemRequestDto> issuedItemDtos, EquipmentTransaction transaction) {
        return issuedItemDtos.stream().map(itemDto -> {
            Equipment equipment = equipmentRepository.findByAssetNumber(itemDto.getAssetNumber())
                    .orElseThrow(() -> new RuntimeException("Equipment not found with asset number: " + itemDto.getAssetNumber()));

            // Validate equipment is available
            if (equipment.getStatus() != Equipment.EquipmentStatus.AVAILABLE) {
                throw new EquipmentUnavailableException(
                        "Equipment " + itemDto.getAssetNumber() + " is not available. Current status: " + equipment.getStatus()
                );
            }

            equipmentRepository.save(equipment);

            TransactionIssuedItem issuedItem = TransactionIssuedItem.builder()
                    .transaction(transaction)
                    .equipment(equipment)
                    .accessoriesProvided(itemDto.getAccessoriesProvided())
                    .build();

            return issuedItemRepository.save(issuedItem);
        }).collect(Collectors.toList());
    }

    private List<TransactionReturnedItem> processReturnedItems(List<ReturnedItemRequestDto> returnedItemDtos, EquipmentTransaction transaction) {
        return returnedItemDtos.stream().map(itemDto -> {
            Equipment equipment = equipmentRepository.findByAssetNumber(itemDto.getAssetNumber())
                    .orElseThrow(() -> new RuntimeException("Equipment not found with asset number: " + itemDto.getAssetNumber()));

            // Validate equipment is issued
            if (equipment.getStatus() != Equipment.EquipmentStatus.ISSUED) {
                throw new EquipmentUnavailableException(
                        "Equipment " + itemDto.getAssetNumber() + " is not in ISSUED status. Current status: " + equipment.getStatus()
                );
            }

            equipmentRepository.save(equipment);

            TransactionReturnedItem returnedItem = TransactionReturnedItem.builder()
                    .transaction(transaction)
                    .equipment(equipment)
                    .itemCondition(itemDto.getItemCondition())
                    .remarks(itemDto.getRemarks())
                    .build();

            return returnedItemRepository.save(returnedItem);
        }).collect(Collectors.toList());
    }

    private IctChecklist processChecklist(IctChecklistRequestDto checklistDto, EquipmentTransaction transaction) {
        IctChecklist checklist = IctChecklist.builder()
                .transaction(transaction)
                .osInstalled(checklistDto.getOsInstalled())
                .appSystemInstalled(checklistDto.getAppSystemInstalled())
                .antiVirusInstalled(checklistDto.getAntiVirusInstalled())
                .pdfReaderInstalled(checklistDto.getPdfReaderInstalled())
                .isJoinedToDomain(checklistDto.getIsJoinedToDomain())
                .isInstalledVpn(checklistDto.getIsInstalledVpn())
                .isInstalledPrinter(checklistDto.getIsInstalledPrinter())
                .additionalNotes(checklistDto.getAdditionalNotes())
                .build();

        return checklistRepository.save(checklist);
    }

    private void rollbackEquipmentStatuses(EquipmentTransaction transaction) {
        log.info("No equipment status rollback needed for transaction {}", transaction.getTransactionCode());
    }

    private String generateTransactionCode() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "TXN-" + timestamp + "-" + uuid;
    }

    private TransactionResponseDto mapToResponseDto(EquipmentTransaction transaction, String staffName, String officerName) {
        List<IssuedItemResponseDto> issuedItemDtos = null;
        if (transaction.getIssuedItems() != null) {
            issuedItemDtos = transaction.getIssuedItems().stream()
                    .map(item -> IssuedItemResponseDto.builder()
                            .id(item.getId())
                            .assetNumber(item.getEquipment().getAssetNumber())
                            .serialNumber(item.getEquipment().getSerialNumber())
                            .equipmentType(item.getEquipment().getEquipmentType())
                            .accessoriesProvided(item.getAccessoriesProvided())
                            .build())
                    .collect(Collectors.toList());
        }

        List<ReturnedItemResponseDto> returnedItemDtos = null;
        if (transaction.getReturnedItems() != null) {
            returnedItemDtos = transaction.getReturnedItems().stream()
                    .map(item -> ReturnedItemResponseDto.builder()
                            .id(item.getId())
                            .assetNumber(item.getEquipment().getAssetNumber())
                            .serialNumber(item.getEquipment().getSerialNumber())
                            .equipmentType(item.getEquipment().getEquipmentType())
                            .itemCondition(item.getItemCondition().name())
                            .remarks(item.getRemarks())
                            .build())
                    .collect(Collectors.toList());
        }

        IctChecklistResponseDto checklistDto = null;
        if (transaction.getChecklist() != null) {
            IctChecklist checklist = transaction.getChecklist();
            checklistDto = IctChecklistResponseDto.builder()
                    .id(checklist.getId())
                    .osInstalled(checklist.getOsInstalled())
                    .appSystemInstalled(checklist.getAppSystemInstalled())
                    .antiVirusInstalled(checklist.getAntiVirusInstalled())
                    .pdfReaderInstalled(checklist.getPdfReaderInstalled())
                    .isJoinedToDomain(checklist.getIsJoinedToDomain())
                    .isInstalledVpn(checklist.getIsInstalledVpn())
                    .isInstalledPrinter(checklist.getIsInstalledPrinter())
                    .additionalNotes(checklist.getAdditionalNotes())
                    .build();
        }

        return TransactionResponseDto.builder()
                .id(transaction.getId())
                .transactionCode(transaction.getTransactionCode())
                .staffId(transaction.getStaffId())
                .staffName(staffName)
                .issuingOfficerId(transaction.getIssuingOfficerId())
                .issuingOfficerName(officerName)
                .status(transaction.getStatus().name())
                .employeeSigned(transaction.getEmployeeSigned() != null ? transaction.getEmployeeSigned() : false)
                .officerSigned(transaction.getOfficerSigned() != null ? transaction.getOfficerSigned() : false)
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .issuedItems(issuedItemDtos)
                .returnedItems(returnedItemDtos)
                .checklist(checklistDto)
                .build();
    }
}
