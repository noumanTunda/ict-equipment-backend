package com.tundalabs.ictequipment.service;

import com.tundalabs.ictequipment.dto.*;
import com.tundalabs.ictequipment.entity.EquipmentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EquipmentRequestService {

    EquipmentRequestResponseDto createRequest(Long staffId, CreateEquipmentRequestDto request);

    EquipmentRequestResponseDto getRequestById(Long id);

    EquipmentRequestResponseDto getRequestByCode(String requestCode);

    List<EquipmentRequestResponseDto> getMyRequests(Long staffId);

    Page<EquipmentRequestResponseDto> getMyRequests(Long staffId, Pageable pageable);

    Page<EquipmentRequestResponseDto> getAllRequests(EquipmentRequest.RequestStatus status, Pageable pageable);

    EquipmentRequestResponseDto approveRequest(Long officerId, ApproveEquipmentRequestDto request);

    EquipmentRequestResponseDto rejectRequest(Long officerId, Long requestId, RejectEquipmentRequestDto request);
}
