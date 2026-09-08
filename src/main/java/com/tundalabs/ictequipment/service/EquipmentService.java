package com.tundalabs.ictequipment.service;

import com.tundalabs.ictequipment.dto.EquipmentRequestDto;
import com.tundalabs.ictequipment.dto.EquipmentResponseDto;
import com.tundalabs.ictequipment.dto.EquipmentSearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EquipmentService {

    EquipmentResponseDto createEquipment(EquipmentRequestDto request);

    EquipmentResponseDto getEquipmentById(Long id);

    EquipmentResponseDto getEquipmentByAssetNumber(String assetNumber);

    List<EquipmentResponseDto> getAllEquipment();

    Page<EquipmentResponseDto> getEquipment(Pageable pageable);

    EquipmentResponseDto updateEquipment(Long id, EquipmentRequestDto request);

    void deleteEquipment(Long id);

    List<EquipmentSearchResponseDto> searchEquipment(String status, String query);

    List<EquipmentResponseDto> getMyIssuedItems();
}
