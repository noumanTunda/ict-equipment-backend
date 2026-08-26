package com.tundalabs.ictequipment.repository;

import com.tundalabs.ictequipment.entity.EquipmentTransaction;
import com.tundalabs.ictequipment.entity.EquipmentTransaction.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentTransactionRepository extends JpaRepository<EquipmentTransaction, Long> {

    Optional<EquipmentTransaction> findByTransactionCode(String transactionCode);

    List<EquipmentTransaction> findByStaffId(Long staffId);

    List<EquipmentTransaction> findByIssuingOfficerId(Long issuingOfficerId);

    List<EquipmentTransaction> findByStatus(TransactionStatus status);

    Page<EquipmentTransaction> findByStaffId(Long staffId, Pageable pageable);

    Page<EquipmentTransaction> findByStatus(TransactionStatus status, Pageable pageable);

    @Query("SELECT t FROM EquipmentTransaction t WHERE " +
           "(:staffId IS NULL OR t.staffId = :staffId) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:startDate IS NULL OR t.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR t.createdAt <= :endDate)")
    Page<EquipmentTransaction> findByFilters(
            @Param("staffId") Long staffId,
            @Param("status") TransactionStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    boolean existsByTransactionCode(String transactionCode);

    @Query("SELECT COUNT(t) FROM EquipmentTransaction t WHERE t.staffId = :staffId AND t.status = :status")
    long countByStaffIdAndStatus(@Param("staffId") Long staffId, @Param("status") TransactionStatus status);
}
