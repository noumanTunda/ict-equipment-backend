package com.tundalabs.ictequipment.repository;

import com.tundalabs.ictequipment.entity.EquipmentRequest;
import com.tundalabs.ictequipment.entity.EquipmentRequest.RequestStatus;
import com.tundalabs.ictequipment.entity.EquipmentRequest.RequestType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRequestRepository extends JpaRepository<EquipmentRequest, Long> {

    Optional<EquipmentRequest> findByRequestCode(String requestCode);

    List<EquipmentRequest> findByStaffId(Long staffId);

    Page<EquipmentRequest> findByStaffId(Long staffId, Pageable pageable);

    List<EquipmentRequest> findByStatus(RequestStatus status);

    Page<EquipmentRequest> findByStatus(RequestStatus status, Pageable pageable);

    List<EquipmentRequest> findByStaffIdAndStatus(Long staffId, RequestStatus status);

    @Query("SELECT r FROM EquipmentRequest r WHERE r.staffId = :staffId AND r.requestType = :type ORDER BY r.createdAt DESC")
    List<EquipmentRequest> findByStaffIdAndRequestType(@Param("staffId") Long staffId, @Param("type") RequestType type);

    @Query("SELECT r FROM EquipmentRequest r WHERE r.status = :status ORDER BY r.createdAt DESC")
    Page<EquipmentRequest> findByStatusOrderByCreatedAtDesc(@Param("status") RequestStatus status, Pageable pageable);

    boolean existsByRequestCode(String requestCode);
}
