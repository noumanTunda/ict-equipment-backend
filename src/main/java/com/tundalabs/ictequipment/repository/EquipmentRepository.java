package com.tundalabs.ictequipment.repository;

import com.tundalabs.ictequipment.entity.Equipment;
import com.tundalabs.ictequipment.entity.Equipment.EquipmentStatus;
import com.tundalabs.ictequipment.projection.AssetStatusProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long>, JpaSpecificationExecutor<Equipment> {

    Optional<Equipment> findByAssetNumber(String assetNumber);

    Optional<Equipment> findBySerialNumber(String serialNumber);

    List<Equipment> findByStatus(EquipmentStatus status);

    @Query("SELECT e FROM Equipment e WHERE e.status = :status AND e.equipmentType = :type")
    List<Equipment> findByStatusAndEquipmentType(@Param("status") EquipmentStatus status, @Param("type") String type);

    boolean existsByAssetNumber(String assetNumber);

    boolean existsBySerialNumber(String serialNumber);

    @Query("SELECT e FROM Equipment e WHERE e.assetNumber IN :assetNumbers")
    List<Equipment> findByAssetNumbers(@Param("assetNumbers") List<String> assetNumbers);

    @Query("SELECT e FROM Equipment e WHERE e.status = :status AND (LOWER(e.equipmentType) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(e.assetNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(e.serialNumber) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Equipment> searchByStatusAndQuery(@Param("status") EquipmentStatus status, @Param("query") String query);

    @Query("""
        SELECT 
            e.id as id,
            e.assetNumber as assetNumber,
            e.serialNumber as serialNumber,
            e.equipmentType as equipmentType,
            e.status as status,
            t.createdAt as issuedAt,
            tri.createdAt as returnedAt,
            t.status as transactionStatus,
            t.transactionCode as transactionCode,
            t.staffId as staffId
        FROM Equipment e
        LEFT JOIN TransactionIssuedItem tii ON e.id = tii.equipment.id
        LEFT JOIN EquipmentTransaction t ON tii.transaction.id = t.id
        LEFT JOIN TransactionReturnedItem tri ON e.id = tri.equipment.id AND tri.transaction.id = t.id
        WHERE t.staffId = :staffId
        ORDER BY t.createdAt DESC
        """)
    List<AssetStatusProjection> findStaffAssignedAssetsWithStatus(@Param("staffId") Long staffId);
}
