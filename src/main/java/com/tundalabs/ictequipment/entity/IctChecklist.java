package com.tundalabs.ictequipment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ict_checklists")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IctChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", unique = true, nullable = false)
    private EquipmentTransaction transaction;

    @Column(name = "os_installed", length = 100)
    private String osInstalled;

    @Column(name = "app_system_installed", length = 100)
    private String appSystemInstalled;

    @Column(name = "anti_virus_installed", length = 100)
    private String antiVirusInstalled;

    @Column(name = "pdf_reader_installed", length = 100)
    private String pdfReaderInstalled;

    @Column(name = "is_joined_to_domain")
    private Boolean isJoinedToDomain;

    @Column(name = "is_installed_vpn")
    private Boolean isInstalledVpn;

    @Column(name = "is_installed_printer")
    private Boolean isInstalledPrinter;

    @Column(name = "additional_notes", length = 500)
    private String additionalNotes;

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
