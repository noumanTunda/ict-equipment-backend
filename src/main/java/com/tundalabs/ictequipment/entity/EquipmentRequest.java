package com.tundalabs.ictequipment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "equipment_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_code", unique = true, nullable = false, length = 50)
    private String requestCode;

    @Column(name = "staff_id", nullable = false)
    private Long staffId;

    @Column(name = "request_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RequestType requestType;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "return_asset_number", length = 50)
    private String returnAssetNumber;

    @Column(name = "issue_asset_number", length = 50)
    private String issueAssetNumber;

    @Column(name = "preferred_equipment_type", length = 100)
    private String preferredEquipmentType;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "transaction_id")
    private Long transactionId;

    public enum RequestType {
        ISSUE,
        RETURN,
        EXCHANGE
    }

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED,
        COMPLETED
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
}
