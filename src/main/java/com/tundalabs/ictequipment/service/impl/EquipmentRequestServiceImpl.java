package com.tundalabs.ictequipment.service.impl;

import com.tundalabs.ictequipment.dto.*;
import com.tundalabs.ictequipment.entity.*;
import com.tundalabs.ictequipment.exception.EquipmentUnavailableException;
import com.tundalabs.ictequipment.exception.InvalidTransactionStateException;
import com.tundalabs.ictequipment.repository.*;
import com.tundalabs.ictequipment.service.EquipmentRequestService;
import com.tundalabs.ictequipment.service.EquipmentTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EquipmentRequestServiceImpl implements EquipmentRequestService {

    private final EquipmentRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EquipmentRepository equipmentRepository;
    private final EquipmentTransactionService transactionService;
    private final EquipmentTransactionRepository transactionRepository;
    private final TransactionIssuedItemRepository issuedItemRepository;
    private final TransactionReturnedItemRepository returnedItemRepository;
    private final IctChecklistRepository checklistRepository;

    @Override
    @Transactional
    public EquipmentRequestResponseDto createRequest(Long staffId, CreateEquipmentRequestDto request) {
        log.info("Creating equipment request for staff ID: {}, type: {}", staffId, request.getRequestType());

        // Validate staff exists
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff member not found with ID: " + staffId));

        // Validate equipment exists if provided
        if (request.getReturnAssetNumber() != null) {
            Equipment returnEquipment = equipmentRepository.findByAssetNumber(request.getReturnAssetNumber())
                    .orElseThrow(() -> new RuntimeException("Equipment not found with asset number: " + request.getReturnAssetNumber()));
            
            // For RETURN or EXCHANGE, validate equipment is ISSUED to this staff
            if (request.getRequestType() == EquipmentRequest.RequestType.RETURN || 
                request.getRequestType() == EquipmentRequest.RequestType.EXCHANGE) {
                if (returnEquipment.getStatus() != Equipment.EquipmentStatus.ISSUED) {
                    throw new EquipmentUnavailableException(
                            "Equipment " + request.getReturnAssetNumber() + " is not in ISSUED status. Current status: " + returnEquipment.getStatus()
                    );
                }
            }
        }

        if (request.getIssueAssetNumber() != null) {
            Equipment issueEquipment = equipmentRepository.findByAssetNumber(request.getIssueAssetNumber())
                    .orElseThrow(() -> new RuntimeException("Equipment not found with asset number: " + request.getIssueAssetNumber()));
            
            // For ISSUE or EXCHANGE, validate equipment is AVAILABLE
            if (request.getRequestType() == EquipmentRequest.RequestType.ISSUE || 
                request.getRequestType() == EquipmentRequest.RequestType.EXCHANGE) {
                if (issueEquipment.getStatus() != Equipment.EquipmentStatus.AVAILABLE) {
                    throw new EquipmentUnavailableException(
                            "Equipment " + request.getIssueAssetNumber() + " is not available. Current status: " + issueEquipment.getStatus()
                    );
                }
            }
        }

        // Generate unique request code
        String requestCode = generateRequestCode();

        // Create request
        EquipmentRequest equipmentRequest = EquipmentRequest.builder()
                .requestCode(requestCode)
                .staffId(staffId)
                .requestType(request.getRequestType())
                .reason(request.getReason())
                .returnAssetNumber(request.getReturnAssetNumber())
                .issueAssetNumber(request.getIssueAssetNumber())
                .preferredEquipmentType(request.getPreferredEquipmentType())
                .status(EquipmentRequest.RequestStatus.PENDING)
                .build();

        equipmentRequest = requestRepository.save(equipmentRequest);
        log.info("Equipment request created successfully with code: {}", requestCode);

        return mapToResponseDto(equipmentRequest, staff.getFullName(), null, null);
    }

    @Override
    public EquipmentRequestResponseDto getRequestById(Long id) {
        EquipmentRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment request not found with ID: " + id));
        
        User staff = userRepository.findById(request.getStaffId()).orElse(null);
        User approvedBy = request.getApprovedBy() != null ? userRepository.findById(request.getApprovedBy()).orElse(null) : null;
        
        return mapToResponseDto(request, 
                staff != null ? staff.getFullName() : null, 
                approvedBy != null ? approvedBy.getFullName() : null,
                null);
    }

    @Override
    public EquipmentRequestResponseDto getRequestByCode(String requestCode) {
        EquipmentRequest request = requestRepository.findByRequestCode(requestCode)
                .orElseThrow(() -> new RuntimeException("Equipment request not found with code: " + requestCode));
        
        User staff = userRepository.findById(request.getStaffId()).orElse(null);
        User approvedBy = request.getApprovedBy() != null ? userRepository.findById(request.getApprovedBy()).orElse(null) : null;
        
        return mapToResponseDto(request, 
                staff != null ? staff.getFullName() : null, 
                approvedBy != null ? approvedBy.getFullName() : null,
                null);
    }

    @Override
    public List<EquipmentRequestResponseDto> getMyRequests(Long staffId) {
        List<EquipmentRequest> requests = requestRepository.findByStaffId(staffId);
        User staff = userRepository.findById(staffId).orElse(null);
        
        return requests.stream()
                .map(req -> {
                    User approvedBy = req.getApprovedBy() != null ? userRepository.findById(req.getApprovedBy()).orElse(null) : null;
                    return mapToResponseDto(req, 
                            staff != null ? staff.getFullName() : null,
                            approvedBy != null ? approvedBy.getFullName() : null,
                            null);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<EquipmentRequestResponseDto> getMyRequests(Long staffId, Pageable pageable) {
        Page<EquipmentRequest> requests = requestRepository.findByStaffId(staffId, pageable);
        User staff = userRepository.findById(staffId).orElse(null);
        
        return requests.map(req -> {
            User approvedBy = req.getApprovedBy() != null ? userRepository.findById(req.getApprovedBy()).orElse(null) : null;
            return mapToResponseDto(req, 
                    staff != null ? staff.getFullName() : null,
                    approvedBy != null ? approvedBy.getFullName() : null,
                    null);
        });
    }

    @Override
    public Page<EquipmentRequestResponseDto> getAllRequests(EquipmentRequest.RequestStatus status, Pageable pageable) {
        Page<EquipmentRequest> requests = requestRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        
        return requests.map(req -> {
            User staff = userRepository.findById(req.getStaffId()).orElse(null);
            User approvedBy = req.getApprovedBy() != null ? userRepository.findById(req.getApprovedBy()).orElse(null) : null;
            return mapToResponseDto(req, 
                    staff != null ? staff.getFullName() : null,
                    approvedBy != null ? approvedBy.getFullName() : null,
                    null);
        });
    }

    @Override
    @Transactional
    public EquipmentRequestResponseDto approveRequest(Long officerId, ApproveEquipmentRequestDto request) {
        log.info("Approving equipment request ID: {} by officer ID: {}", request.getRequestId(), officerId);

        // Validate request exists and is pending
        EquipmentRequest equipmentRequest = requestRepository.findById(request.getRequestId())
                .orElseThrow(() -> new RuntimeException("Equipment request not found with ID: " + request.getRequestId()));

        if (equipmentRequest.getStatus() != EquipmentRequest.RequestStatus.PENDING) {
            throw new InvalidTransactionStateException(
                    "Request must be in PENDING status to approve. Current status: " + equipmentRequest.getStatus()
            );
        }

        // Validate officer exists
        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Officer not found with ID: " + officerId));

        // Validate staff exists and get staff ID before modifying request
        Long staffId = equipmentRequest.getStaffId();
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff member not found with ID: " + staffId));

        // Create transaction based on request type
        TransactionResponseDto transactionResponse = createTransactionFromRequest(equipmentRequest, request, officerId, staffId);

        // Update request status
        equipmentRequest.setStatus(EquipmentRequest.RequestStatus.APPROVED);
        equipmentRequest.setApprovedBy(officerId);
        equipmentRequest.setApprovedAt(LocalDateTime.now());
        equipmentRequest.setTransactionId(transactionResponse.getId());
        equipmentRequest = requestRepository.save(equipmentRequest);

        log.info("Equipment request approved successfully with code: {}", equipmentRequest.getRequestCode());

        return mapToResponseDto(equipmentRequest, staff.getFullName(), officer.getFullName(), transactionResponse.getId());
    }

    @Override
    @Transactional
    public EquipmentRequestResponseDto rejectRequest(Long officerId, Long requestId, RejectEquipmentRequestDto request) {
        log.info("Rejecting equipment request ID: {} by officer ID: {}", requestId, officerId);

        // Validate request exists and is pending
        EquipmentRequest equipmentRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Equipment request not found with ID: " + requestId));

        if (equipmentRequest.getStatus() != EquipmentRequest.RequestStatus.PENDING) {
            throw new InvalidTransactionStateException(
                    "Request must be in PENDING status to reject. Current status: " + equipmentRequest.getStatus()
            );
        }

        // Validate officer exists
        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Officer not found with ID: " + officerId));

        // Update request status
        equipmentRequest.setStatus(EquipmentRequest.RequestStatus.REJECTED);
        equipmentRequest.setApprovedBy(officerId);
        equipmentRequest.setApprovedAt(LocalDateTime.now());
        equipmentRequest.setRejectionReason(request.getRejectionReason());
        equipmentRequest = requestRepository.save(equipmentRequest);

        log.info("Equipment request rejected successfully with code: {}", equipmentRequest.getRequestCode());

        User staff = userRepository.findById(equipmentRequest.getStaffId()).orElse(null);
        return mapToResponseDto(equipmentRequest, 
                staff != null ? staff.getFullName() : null, 
                officer.getFullName(),
                null);
    }

    private TransactionResponseDto createTransactionFromRequest(EquipmentRequest equipmentRequest, 
            ApproveEquipmentRequestDto approvalRequest, Long officerId, Long staffId) {
        
        // Generate unique transaction code
        String transactionCode = generateTransactionCode();

        // Create transaction
        EquipmentTransaction transaction = EquipmentTransaction.builder()
                .transactionCode(transactionCode)
                .staffId(staffId)
                .issuingOfficerId(officerId)
                .status(EquipmentTransaction.TransactionStatus.PENDING_SIGNATURE)
                .build();

        transaction = transactionRepository.save(transaction);

        // Get asset numbers from approval request (officer's choice) or fall back to request (staff's preference)
        String issueAssetNum = approvalRequest.getIssueAssetNumber() != null ? 
                approvalRequest.getIssueAssetNumber() : equipmentRequest.getIssueAssetNumber();
        String returnAssetNum = approvalRequest.getReturnAssetNumber() != null ? 
                approvalRequest.getReturnAssetNumber() : equipmentRequest.getReturnAssetNumber();

        // Process based on request type
        if (equipmentRequest.getRequestType() == EquipmentRequest.RequestType.ISSUE) {
            // Issue new equipment
            if (issueAssetNum == null) {
                throw new RuntimeException("Asset number must be specified for ISSUE request");
            }
            Equipment equipment = equipmentRepository.findByAssetNumber(issueAssetNum)
                    .orElseThrow(() -> new RuntimeException("Equipment not found with asset number: " + issueAssetNum));
            
            // Update equipment status to ISSUED
            equipment.setStatus(Equipment.EquipmentStatus.ISSUED);
            equipmentRepository.save(equipment);

            TransactionIssuedItem issuedItem = TransactionIssuedItem.builder()
                    .transaction(transaction)
                    .equipment(equipment)
                    .accessoriesProvided(approvalRequest.getAccessoriesProvided())
                    .build();
            issuedItemRepository.save(issuedItem);
            transaction.setIssuedItems(List.of(issuedItem));

        } else if (equipmentRequest.getRequestType() == EquipmentRequest.RequestType.RETURN) {
            // Return equipment
            if (returnAssetNum == null) {
                throw new RuntimeException("Asset number must be specified for RETURN request");
            }
            Equipment equipment = equipmentRepository.findByAssetNumber(returnAssetNum)
                    .orElseThrow(() -> new RuntimeException("Equipment not found with asset number: " + returnAssetNum));
            
            // Update equipment status to RETURNED
            equipment.setStatus(Equipment.EquipmentStatus.RETURNED);
            equipmentRepository.save(equipment);

            TransactionReturnedItem returnedItem = TransactionReturnedItem.builder()
                    .transaction(transaction)
                    .equipment(equipment)
                    .itemCondition(approvalRequest.getReturnCondition())
                    .remarks(approvalRequest.getReturnRemarks())
                    .build();
            returnedItemRepository.save(returnedItem);
            transaction.setReturnedItems(List.of(returnedItem));

        } else if (equipmentRequest.getRequestType() == EquipmentRequest.RequestType.EXCHANGE) {
            // Return old equipment
            if (returnAssetNum == null) {
                throw new RuntimeException("Return asset number must be specified for EXCHANGE request");
            }
            Equipment returnEquipment = equipmentRepository.findByAssetNumber(returnAssetNum)
                    .orElseThrow(() -> new RuntimeException("Return equipment not found with asset number: " + returnAssetNum));
            
            returnEquipment.setStatus(Equipment.EquipmentStatus.RETURNED);
            equipmentRepository.save(returnEquipment);

            TransactionReturnedItem returnedItem = TransactionReturnedItem.builder()
                    .transaction(transaction)
                    .equipment(returnEquipment)
                    .itemCondition(approvalRequest.getReturnCondition())
                    .remarks(approvalRequest.getReturnRemarks())
                    .build();
            returnedItemRepository.save(returnedItem);
            transaction.setReturnedItems(List.of(returnedItem));

            // Issue new equipment
            if (issueAssetNum == null) {
                throw new RuntimeException("Issue asset number must be specified for EXCHANGE request");
            }
            Equipment issueEquipment = equipmentRepository.findByAssetNumber(issueAssetNum)
                    .orElseThrow(() -> new RuntimeException("Issue equipment not found with asset number: " + issueAssetNum));
            
            issueEquipment.setStatus(Equipment.EquipmentStatus.ISSUED);
            equipmentRepository.save(issueEquipment);

            TransactionIssuedItem issuedItem = TransactionIssuedItem.builder()
                    .transaction(transaction)
                    .equipment(issueEquipment)
                    .accessoriesProvided(approvalRequest.getAccessoriesProvided())
                    .build();
            issuedItemRepository.save(issuedItem);
            transaction.setIssuedItems(List.of(issuedItem));
        }

        // Process ICT checklist
        if (approvalRequest.getChecklist() != null) {
            IctChecklist checklist = processChecklist(approvalRequest.getChecklist(), transaction);
            transaction.setChecklist(checklist);
        }

        transaction = transactionRepository.save(transaction);

        // Map to response
        User staff = userRepository.findById(staffId).orElseThrow();
        User officer = userRepository.findById(officerId).orElseThrow();
        return mapToTransactionResponseDto(transaction, staff.getFullName(), officer.getFullName());
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

    private String generateRequestCode() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "REQ-" + timestamp + "-" + uuid;
    }

    private String generateTransactionCode() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "TXN-" + timestamp + "-" + uuid;
    }

    private EquipmentRequestResponseDto mapToResponseDto(EquipmentRequest request, String staffName, 
            String approvedByName, Long transactionId) {
        return EquipmentRequestResponseDto.builder()
                .id(request.getId())
                .requestCode(request.getRequestCode())
                .staffId(request.getStaffId())
                .staffName(staffName)
                .requestType(request.getRequestType())
                .reason(request.getReason())
                .status(request.getStatus())
                .rejectionReason(request.getRejectionReason())
                .approvedBy(request.getApprovedBy())
                .approvedByName(approvedByName)
                .approvedAt(request.getApprovedAt())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .transactionId(transactionId != null ? transactionId : request.getTransactionId())
                .build();
    }

    private TransactionResponseDto mapToTransactionResponseDto(EquipmentTransaction transaction, 
            String staffName, String officerName) {
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
                .build();
    }
}
