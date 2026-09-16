package com.tundalabs.ictequipment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "equipment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asset_number", unique = true, nullable = false, length = 50)
    private String assetNumber;

    @Column(name = "serial_number", unique = true, nullable = false, length = 100)
    private String serialNumber;

    @Column(name = "equipment_type", nullable = false, length = 100)
    private String equipmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "department", nullable = false, length = 50)
    private EquipmentDepartment department;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EquipmentStatus status;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "brand_model", length = 100)
    private String brandModel;

    @Column(name = "supplier_details", length = 255)
    private String supplierDetails;

    @Column(name = "has_warranty", nullable = false)
    private Boolean hasWarranty;

    @Column(name = "warranty_duration_months")
    @Min(value = 0, message = "Warranty duration must be non-negative")
    private Integer warrantyDurationMonths;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum EquipmentStatus {
        AVAILABLE,
        ISSUED,
        RETURNED,
        DISPOSED,
        MAINTENANCE
    }

    public enum EquipmentDepartment {
        ICT,
        FINANCE_AND_ACCOUNTS,
        LEGAL_SERVICES,
        HUMAN_RESOURCE_AND_ADMINISTRATION,
        PLANNING_AND_COORDINATION
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @AssertTrue(message = "Warranty duration must be specified when warranty is enabled, and must be null when warranty is disabled")
    private boolean isWarrantyCoDependencyValid() {
        if (hasWarranty == null) {
            return false;
        }
        if (hasWarranty) {
            return warrantyDurationMonths != null && warrantyDurationMonths >= 0;
        }
        return warrantyDurationMonths == null;
    }
}
