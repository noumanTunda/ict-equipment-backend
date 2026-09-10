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

    @Query("SELECT t FROM EquipmentTransaction t " +
           "LEFT JOIN FETCH t.issuedItems ii " +
           "LEFT JOIN FETCH ii.equipment " +
           "LEFT JOIN FETCH t.returnedItems ri " +
           "LEFT JOIN FETCH ri.equipment " +
           "LEFT JOIN FETCH t.checklist " +
           "WHERE t.transactionCode = :transactionCode")
    Optional<EquipmentTransaction> findByTransactionCode(@Param("transactionCode") String transactionCode);

    @Query("SELECT t FROM EquipmentTransaction t " +
           "LEFT JOIN FETCH t.issuedItems ii " +
           "LEFT JOIN FETCH ii.equipment " +
           "LEFT JOIN FETCH t.returnedItems ri " +
           "LEFT JOIN FETCH ri.equipment " +
           "LEFT JOIN FETCH t.checklist " +
           "WHERE t.id = :id")
    Optional<EquipmentTransaction> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT t FROM EquipmentTransaction t " +
           "LEFT JOIN FETCH t.issuedItems ii " +
           "LEFT JOIN FETCH ii.equipment " +
           "LEFT JOIN FETCH t.returnedItems ri " +
           "LEFT JOIN FETCH ri.equipment " +
           "LEFT JOIN FETCH t.checklist " +
           "WHERE t.staffId = :staffId")
    List<EquipmentTransaction> findByStaffId(@Param("staffId") Long staffId);

    @Query("SELECT t FROM EquipmentTransaction t " +
           "LEFT JOIN FETCH t.issuedItems ii " +
           "LEFT JOIN FETCH ii.equipment " +
           "LEFT JOIN FETCH t.returnedItems ri " +
           "LEFT JOIN FETCH ri.equipment " +
           "LEFT JOIN FETCH t.checklist " +
           "WHERE t.issuingOfficerId = :issuingOfficerId")
    List<EquipmentTransaction> findByIssuingOfficerId(@Param("issuingOfficerId") Long issuingOfficerId);

    @Query("SELECT t FROM EquipmentTransaction t " +
           "LEFT JOIN FETCH t.issuedItems ii " +
           "LEFT JOIN FETCH ii.equipment " +
           "LEFT JOIN FETCH t.returnedItems ri " +
           "LEFT JOIN FETCH ri.equipment " +
           "LEFT JOIN FETCH t.checklist " +
           "WHERE t.status = :status")
    List<EquipmentTransaction> findByStatus(@Param("status") TransactionStatus status);

    @Query("SELECT t FROM EquipmentTransaction t " +
           "LEFT JOIN FETCH t.issuedItems ii " +
           "LEFT JOIN FETCH ii.equipment " +
           "LEFT JOIN FETCH t.returnedItems ri " +
           "LEFT JOIN FETCH ri.equipment " +
           "LEFT JOIN FETCH t.checklist " +
           "WHERE t.staffId = :staffId")
    Page<EquipmentTransaction> findByStaffId(@Param("staffId") Long staffId, Pageable pageable);

    @Query("SELECT t FROM EquipmentTransaction t " +
           "LEFT JOIN FETCH t.issuedItems ii " +
           "LEFT JOIN FETCH ii.equipment " +
           "LEFT JOIN FETCH t.returnedItems ri " +
           "LEFT JOIN FETCH ri.equipment " +
           "LEFT JOIN FETCH t.checklist " +
           "WHERE t.status = :status")
    Page<EquipmentTransaction> findByStatus(@Param("status") TransactionStatus status, Pageable pageable);

    @Query("SELECT t FROM EquipmentTransaction t " +
           "LEFT JOIN FETCH t.issuedItems ii " +
           "LEFT JOIN FETCH ii.equipment " +
           "LEFT JOIN FETCH t.returnedItems ri " +
           "LEFT JOIN FETCH ri.equipment " +
           "LEFT JOIN FETCH t.checklist " +
           "WHERE (:staffId IS NULL OR t.staffId = :staffId) AND " +
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
