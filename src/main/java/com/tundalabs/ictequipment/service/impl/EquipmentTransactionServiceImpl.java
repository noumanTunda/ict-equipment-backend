package com.tundalabs.ictequipment.service.impl;

import com.lowagie.text.*;
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

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
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
            throw new InvalidTransactionStateException("Staff member has not set a keyphrase. Please set a keyphrase first.");
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
        if (transaction.getEmployeeSigned() && transaction.getOfficerSigned()) {
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
                    "Transaction must be in PENDING_SIGNATURE status to submit signatures. Current status: " + transaction.getStatus()
            );
        }

        // Validate at least one signature is provided
        if ((request.getEmployeeSignature() == null || request.getEmployeeSignature().isEmpty()) &&
            (request.getOfficerSignature() == null || request.getOfficerSignature().isEmpty())) {
            throw new RuntimeException("At least one signature is required");
        }

        // Update employee signature if provided
        if (request.getEmployeeSignature() != null && !request.getEmployeeSignature().isEmpty()) {
            transaction.setEmployeeSignature(request.getEmployeeSignature());
            transaction.setEmployeeSignedAt(LocalDateTime.now());
            log.info("Employee signature submitted for transaction ID: {}", transactionId);
        }

        // Update officer signature if provided
        if (request.getOfficerSignature() != null && !request.getOfficerSignature().isEmpty()) {
            transaction.setOfficerSignature(request.getOfficerSignature());
            transaction.setOfficerSignedAt(LocalDateTime.now());
            log.info("Officer signature submitted for transaction ID: {}", transactionId);
        }

        // Check if both signatures are now present - if so, complete the transaction
        if (transaction.getEmployeeSignature() != null && !transaction.getEmployeeSignature().isEmpty() &&
            transaction.getOfficerSignature() != null && !transaction.getOfficerSignature().isEmpty()) {
            
            // Update equipment statuses now that transaction is being completed
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
            log.info("Transaction completed successfully with ID: {}", transactionId);
        }

        transaction = transactionRepository.save(transaction);

        User staff = userRepository.findById(transaction.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff member not found"));
        User officer = userRepository.findById(transaction.getIssuingOfficerId())
                .orElseThrow(() -> new RuntimeException("Issuing officer not found"));

        return mapToResponseDto(transaction, staff.getFullName(), officer.getFullName());
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
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            document.add(new Paragraph("PPRA ICT EQUIPMENT ISSUE AND RETURN FORM", titleFont));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("PART A - TRANSACTION DETAILS", headerFont));
            document.add(new Paragraph("Transaction Code: " + transaction.getTransactionCode(), normalFont));
            document.add(new Paragraph("Date: " + transaction.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), normalFont));
            document.add(new Paragraph("Status: " + transaction.getStatus(), normalFont));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("PART B - STAFF INFORMATION", headerFont));
            document.add(new Paragraph("Staff ID: " + transaction.getStaffId(), normalFont));
            document.add(new Paragraph("Staff Name: " + staff.getFullName(), normalFont));
            document.add(new Paragraph("Department: " + staff.getDepartment(), normalFont));
            document.add(new Paragraph("Email: " + staff.getEmail(), normalFont));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("PART C - ISSUING OFFICER INFORMATION", headerFont));
            document.add(new Paragraph("Officer ID: " + transaction.getIssuingOfficerId(), normalFont));
            document.add(new Paragraph("Officer Name: " + officer.getFullName(), normalFont));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("PART D - ISSUED ITEMS", headerFont));
            if (transaction.getIssuedItems() != null && !transaction.getIssuedItems().isEmpty()) {
                Table issuedTable = new Table(3);
                issuedTable.setWidths(new float[]{3f, 3f, 4f});
                issuedTable.addCell(new Cell(new Phrase("Asset Number", headerFont)));
                issuedTable.addCell(new Cell(new Phrase("Serial Number", headerFont)));
                issuedTable.addCell(new Cell(new Phrase("Accessories", headerFont)));

                for (TransactionIssuedItem item : transaction.getIssuedItems()) {
                    issuedTable.addCell(new Cell(new Phrase(item.getEquipment().getAssetNumber(), normalFont)));
                    issuedTable.addCell(new Cell(new Phrase(item.getEquipment().getSerialNumber(), normalFont)));
                    issuedTable.addCell(new Cell(new Phrase(item.getAccessoriesProvided() != null ? item.getAccessoriesProvided() : "N/A", normalFont)));
                }
                document.add(issuedTable);
            } else {
                document.add(new Paragraph("No issued items", normalFont));
            }
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("PART E - RETURNED ITEMS", headerFont));
            if (transaction.getReturnedItems() != null && !transaction.getReturnedItems().isEmpty()) {
                Table returnedTable = new Table(3);
                returnedTable.setWidths(new float[]{3f, 3f, 4f});
                returnedTable.addCell(new Cell(new Phrase("Asset Number", headerFont)));
                returnedTable.addCell(new Cell(new Phrase("Condition", headerFont)));
                returnedTable.addCell(new Cell(new Phrase("Remarks", headerFont)));

                for (TransactionReturnedItem item : transaction.getReturnedItems()) {
                    returnedTable.addCell(new Cell(new Phrase(item.getEquipment().getAssetNumber(), normalFont)));
                    returnedTable.addCell(new Cell(new Phrase(item.getItemCondition(), normalFont)));
                    returnedTable.addCell(new Cell(new Phrase(item.getRemarks() != null ? item.getRemarks() : "N/A", normalFont)));
                }
                document.add(returnedTable);
            } else {
                document.add(new Paragraph("No returned items", normalFont));
            }
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("PART F - ICT CHECKLIST", headerFont));
            if (transaction.getChecklist() != null) {
                IctChecklist checklist = transaction.getChecklist();
                document.add(new Paragraph("OS Installed: " + (checklist.getOsInstalled() != null ? checklist.getOsInstalled() : "N/A"), normalFont));
                document.add(new Paragraph("App/System Installed: " + (checklist.getAppSystemInstalled() != null ? checklist.getAppSystemInstalled() : "N/A"), normalFont));
                document.add(new Paragraph("Anti-virus Installed: " + (checklist.getAntiVirusInstalled() != null ? checklist.getAntiVirusInstalled() : "N/A"), normalFont));
                document.add(new Paragraph("PDF Reader Installed: " + (checklist.getPdfReaderInstalled() != null ? checklist.getPdfReaderInstalled() : "N/A"), normalFont));
                document.add(new Paragraph("Joined to Domain: " + (checklist.getIsJoinedToDomain() != null ? checklist.getIsJoinedToDomain() : "N/A"), normalFont));
                document.add(new Paragraph("VPN Installed: " + (checklist.getIsInstalledVpn() != null ? checklist.getIsInstalledVpn() : "N/A"), normalFont));
                document.add(new Paragraph("Printer Installed: " + (checklist.getIsInstalledPrinter() != null ? checklist.getIsInstalledPrinter() : "N/A"), normalFont));
                document.add(new Paragraph("Additional Notes: " + (checklist.getAdditionalNotes() != null ? checklist.getAdditionalNotes() : "N/A"), normalFont));
            } else {
                document.add(new Paragraph("No checklist provided", normalFont));
            }
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("PART G - SIGNATURES", headerFont));
            
            // Create 2-column table for signatures
            Table signatureTable = new Table(2);
            signatureTable.setWidths(new float[]{3f, 2f});
            signatureTable.setWidth(100f);
            
            // Employee Signature Row
            Cell employeeMetadataCell = new Cell();
            employeeMetadataCell.setBorder(Rectangle.NO_BORDER);
            employeeMetadataCell.add(new Phrase("Staff Signature:", headerFont));
            employeeMetadataCell.add(Chunk.NEWLINE);
            employeeMetadataCell.add(new Phrase("Name: " + staff.getFullName(), normalFont));
            employeeMetadataCell.add(Chunk.NEWLINE);
            String employeeSignedAt = transaction.getEmployeeSignedAt() != null 
                ? transaction.getEmployeeSignedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) 
                : "Not signed";
            employeeMetadataCell.add(new Phrase("Signed At: " + employeeSignedAt, normalFont));
            signatureTable.addCell(employeeMetadataCell);
            
            Cell employeeSignatureCell = new Cell();
            employeeSignatureCell.setBorder(Rectangle.NO_BORDER);
            Image employeeSignatureImage = createSignatureImage(transaction.getEmployeeSignature());
            if (employeeSignatureImage != null) {
                employeeSignatureCell.add(employeeSignatureImage);
            } else {
                employeeSignatureCell.add(new Phrase("[ No Signature ]", normalFont));
            }
            signatureTable.addCell(employeeSignatureCell);
            
            // Officer Signature Row
            Cell officerMetadataCell = new Cell();
            officerMetadataCell.setBorder(Rectangle.NO_BORDER);
            officerMetadataCell.add(new Phrase("Officer Signature:", headerFont));
            officerMetadataCell.add(Chunk.NEWLINE);
            officerMetadataCell.add(new Phrase("Name: " + officer.getFullName(), normalFont));
            officerMetadataCell.add(Chunk.NEWLINE);
            String officerSignedAt = transaction.getOfficerSignedAt() != null 
                ? transaction.getOfficerSignedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) 
                : "Not signed";
            officerMetadataCell.add(new Phrase("Signed At: " + officerSignedAt, normalFont));
            signatureTable.addCell(officerMetadataCell);
            
            Cell officerSignatureCell = new Cell();
            officerSignatureCell.setBorder(Rectangle.NO_BORDER);
            Image officerSignatureImage = createSignatureImage(transaction.getOfficerSignature());
            if (officerSignatureImage != null) {
                officerSignatureCell.add(officerSignatureImage);
            } else {
                officerSignatureCell.add(new Phrase("[ No Signature ]", normalFont));
            }
            signatureTable.addCell(officerSignatureCell);
            
            document.add(signatureTable);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF for transaction ID: {}", transactionId, e);
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
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
        // No rollback needed since equipment statuses are only updated on signature submission
        // If transaction is cancelled before signatures, equipment statuses remain unchanged
        log.info("No equipment status rollback needed for transaction {}", transaction.getTransactionCode());
    }

    private String generateTransactionCode() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "TXN-" + timestamp + "-" + uuid;
    }

    private Image createSignatureImage(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return null;
        }

        try {
            // Strip data URI header if present
            String sanitizedBase64 = base64String;
            if (base64String.startsWith("data:image/")) {
                int commaIndex = base64String.indexOf(",");
                if (commaIndex != -1) {
                    sanitizedBase64 = base64String.substring(commaIndex + 1);
                }
            }

            // Decode Base64 to byte array
            byte[] imageBytes = Base64.getDecoder().decode(sanitizedBase64);

            // Create OpenPDF Image from bytes
            Image signatureImage = Image.getInstance(imageBytes);
            
            // Scale to uniform dimensions
            signatureImage.scaleToFit(120f, 40f);
            
            return signatureImage;
        } catch (Exception e) {
            log.error("Error decoding signature image from Base64", e);
            return null;
        }
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
                            .itemCondition(item.getItemCondition())
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
                .employeeSignature(transaction.getEmployeeSignature())
                .officerSignature(transaction.getOfficerSignature())
                .employeeSignedAt(transaction.getEmployeeSignedAt())
                .officerSignedAt(transaction.getOfficerSignedAt())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .issuedItems(issuedItemDtos)
                .returnedItems(returnedItemDtos)
                .checklist(checklistDto)
                .build();
    }
}
