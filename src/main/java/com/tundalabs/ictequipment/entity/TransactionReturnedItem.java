package com.tundalabs.ictequipment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_returned_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionReturnedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private EquipmentTransaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Equipment equipment;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_condition", nullable = false, length = 20)
    private ItemCondition itemCondition;

    @Column(name = "remarks", length = 500)
    private String remarks;

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

    public enum ItemCondition {
        GOOD,
        FAIR,
        DAMAGED,
        OBSOLETE
    }
}
