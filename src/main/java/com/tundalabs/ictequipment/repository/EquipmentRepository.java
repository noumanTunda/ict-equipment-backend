package com.tundalabs.ictequipment.repository;

import com.tundalabs.ictequipment.entity.Equipment;
import com.tundalabs.ictequipment.entity.Equipment.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

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
}
