package com.tundalabs.ictequipment.service.impl;

import com.tundalabs.ictequipment.dto.EquipmentRequestDto;
import com.tundalabs.ictequipment.dto.EquipmentResponseDto;
import com.tundalabs.ictequipment.entity.Equipment;
import com.tundalabs.ictequipment.exception.ResourceNotFoundException;
import com.tundalabs.ictequipment.repository.EquipmentRepository;
import com.tundalabs.ictequipment.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;

    @Override
    @Transactional
    public EquipmentResponseDto createEquipment(EquipmentRequestDto request) {
        log.info("Creating equipment with asset number: {}", request.getAssetNumber());

        if (equipmentRepository.existsByAssetNumber(request.getAssetNumber())) {
            throw new IllegalArgumentException("Equipment with asset number " + request.getAssetNumber() + " already exists");
        }

        if (equipmentRepository.existsBySerialNumber(request.getSerialNumber())) {
            throw new IllegalArgumentException("Equipment with serial number " + request.getSerialNumber() + " already exists");
        }

        Equipment equipment = Equipment.builder()
                .assetNumber(request.getAssetNumber())
                .serialNumber(request.getSerialNumber())
                .equipmentType(request.getEquipmentType())
                .brandModel(request.getBrandModel())
                .department(request.getDepartment())
                .supplierDetails(request.getSupplierDetails())
                .status(request.getStatus())
                .description(request.getDescription())
                .build();

        equipment = equipmentRepository.save(equipment);
        log.info("Equipment created successfully with ID: {}", equipment.getId());

        return mapToResponseDto(equipment);
    }

    @Override
    public EquipmentResponseDto getEquipmentById(Long id) {
        log.info("Fetching equipment by ID: {}", id);
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with ID: " + id));
        return mapToResponseDto(equipment);
    }

    @Override
    public EquipmentResponseDto getEquipmentByAssetNumber(String assetNumber) {
        log.info("Fetching equipment by asset number: {}", assetNumber);
        Equipment equipment = equipmentRepository.findByAssetNumber(assetNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with asset number: " + assetNumber));
        return mapToResponseDto(equipment);
    }

    @Override
    public List<EquipmentResponseDto> getAllEquipment() {
        log.info("Fetching all equipment");
        List<Equipment> equipmentList = equipmentRepository.findAll();
        return equipmentList.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<EquipmentResponseDto> getEquipment(Pageable pageable) {
        log.info("Fetching equipment with pagination");
        Page<Equipment> equipmentPage = equipmentRepository.findAll(pageable);
        return equipmentPage.map(this::mapToResponseDto);
    }

    @Override
    @Transactional
    public EquipmentResponseDto updateEquipment(Long id, EquipmentRequestDto request) {
        log.info("Updating equipment with ID: {}", id);

        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with ID: " + id));

        if (!equipment.getAssetNumber().equals(request.getAssetNumber()) && 
            equipmentRepository.existsByAssetNumber(request.getAssetNumber())) {
            throw new IllegalArgumentException("Equipment with asset number " + request.getAssetNumber() + " already exists");
        }

        if (!equipment.getSerialNumber().equals(request.getSerialNumber()) && 
            equipmentRepository.existsBySerialNumber(request.getSerialNumber())) {
            throw new IllegalArgumentException("Equipment with serial number " + request.getSerialNumber() + " already exists");
        }

        equipment.setAssetNumber(request.getAssetNumber());
        equipment.setSerialNumber(request.getSerialNumber());
        equipment.setEquipmentType(request.getEquipmentType());
        equipment.setDepartment(request.getDepartment());
        equipment.setBrandModel(request.getBrandModel());
        equipment.setSupplierDetails(request.getSupplierDetails());
        equipment.setStatus(request.getStatus());
        equipment.setDescription(request.getDescription());

        equipment = equipmentRepository.save(equipment);
        log.info("Equipment updated successfully with ID: {}", id);

        return mapToResponseDto(equipment);
    }

    @Override
    @Transactional
    public void deleteEquipment(Long id) {
        log.info("Deleting equipment with ID: {}", id);

        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with ID: " + id));

        if (equipment.getStatus() == Equipment.EquipmentStatus.ISSUED) {
            throw new IllegalStateException("Cannot delete equipment that is currently issued");
        }

        equipmentRepository.delete(equipment);
        log.info("Equipment deleted successfully with ID: {}", id);
    }

    private EquipmentResponseDto mapToResponseDto(Equipment equipment) {
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
                .createdAt(equipment.getCreatedAt())
                .updatedAt(equipment.getUpdatedAt())
                .build();
    }
}
