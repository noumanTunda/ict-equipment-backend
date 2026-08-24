package com.tundalabs.ictequipment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipment_transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_code", unique = true, nullable = false, length = 50)
    private String transactionCode;

    @Column(name = "staff_id", nullable = false, length = 30)
    private String staffId;

    @Column(name = "issuing_officer_id", nullable = false, length = 30)
    private String issuingOfficerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private TransactionStatus status;

    @Lob
    @Column(name = "employee_signature")
    private String employeeSignature;

    @Lob
    @Column(name = "officer_signature")
    private String officerSignature;

    @Column(name = "employee_signed_at")
    private LocalDateTime employeeSignedAt;

    @Column(name = "officer_signed_at")
    private LocalDateTime officerSignedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TransactionIssuedItem> issuedItems = new ArrayList<>();

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TransactionReturnedItem> returnedItems = new ArrayList<>();

    @OneToOne(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private IctChecklist checklist;

    public enum TransactionStatus {
        DRAFT,
        PENDING_SIGNATURE,
        COMPLETED,
        CANCELLED
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
