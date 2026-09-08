package com.tundalabs.ictequipment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "transaction_issued_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionIssuedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private EquipmentTransaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @ElementCollection
    @CollectionTable(name = "transaction_issued_item_accessories", joinColumns = @JoinColumn(name = "issued_item_id"))
    @Column(name = "accessory")
    private List<String> accessoriesProvided;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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
