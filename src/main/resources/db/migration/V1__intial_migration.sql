-- 1. USERS / STAFF TABLE (Part A Mapping)
CREATE TABLE users
(
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(30)  NOT NULL,
    full_name   VARCHAR(120) NOT NULL,
    department  VARCHAR(80)  NOT NULL,
    position    VARCHAR(80)  NOT NULL,
    mobile_no   VARCHAR(20)  NOT NULL,
    email       VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_users_employee_id UNIQUE (employee_id),
    CONSTRAINT uq_users_email UNIQUE (email)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Optimization Indexes for Staff Lookups
CREATE INDEX idx_users_department ON users (department);
CREATE INDEX idx_users_full_name ON users (full_name);


-- 2. EQUIPMENT MASTER TABLE (Inventory Control)
CREATE TABLE equipment
(
    id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    asset_number     VARCHAR(60)                                                                 NOT NULL,
    serial_number    VARCHAR(60)                                                                 NOT NULL,
    equipment_type   ENUM ('LAPTOP', 'DESKTOP', 'PRINTER', 'UPS', 'SCANNER', 'MONITOR', 'OTHER') NOT NULL,
    brand_model      VARCHAR(100)                                                                NOT NULL,
    supplier_details VARCHAR(255)                                                                         DEFAULT NULL,
    status           ENUM ('AVAILABLE', 'ISSUED', 'RETURNED', 'MAINTENANCE', 'DISPOSED')         NOT NULL DEFAULT 'AVAILABLE',
    created_at       TIMESTAMP                                                                            DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP                                                                            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_equipment_asset_number UNIQUE (asset_number),
    CONSTRAINT uq_equipment_serial_number UNIQUE (serial_number)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Optimization Indexes for Inventory Filtering
CREATE INDEX idx_equipment_status ON equipment (status);
CREATE INDEX idx_equipment_type ON equipment (equipment_type);

-- 3. EQUIPMENT TRANSACTIONS TABLE (Core Process Header & Part D Ack)
CREATE TABLE equipment_transactions
(
    id                 BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    transaction_code   VARCHAR(36)                                                   NOT NULL, -- Format e.g., TX-2026-XXXX
    staff_id           BIGINT UNSIGNED                                               NOT NULL,
    issuing_officer_id BIGINT UNSIGNED                                               NOT NULL,
    status             ENUM ('DRAFT', 'PENDING_SIGNATURE', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'DRAFT',

    -- Part D: Digital Signatures (Stored as Base64 Canvas Image Strings)
    employee_signature MEDIUMTEXT                                                             DEFAULT NULL,
    employee_signed_at DATETIME                                                               DEFAULT NULL,
    officer_signature  MEDIUMTEXT                                                             DEFAULT NULL,
    officer_signed_at  DATETIME                                                               DEFAULT NULL,

    created_at         TIMESTAMP                                                              DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP                                                              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_tx_code UNIQUE (transaction_code),
    CONSTRAINT fk_tx_staff FOREIGN KEY (staff_id)
        REFERENCES users (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_tx_officer FOREIGN KEY (issuing_officer_id)
        REFERENCES users (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Optimization Indexes for Transaction Auditing
CREATE INDEX idx_tx_staff_id ON equipment_transactions (staff_id);
CREATE INDEX idx_tx_officer_id ON equipment_transactions (issuing_officer_id);
CREATE INDEX idx_tx_status ON equipment_transactions (status);
CREATE INDEX idx_tx_created_at ON equipment_transactions (created_at);

-- 4. ISSUED ITEMS TABLE (Part B Mapping)
CREATE TABLE transaction_issued_items
(
    id                   BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    transaction_id       BIGINT UNSIGNED NOT NULL,
    equipment_id         BIGINT UNSIGNED NOT NULL,
    accessories_provided VARCHAR(255) DEFAULT NULL,
    created_at           TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_issued_tx FOREIGN KEY (transaction_id)
        REFERENCES equipment_transactions (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_issued_eq FOREIGN KEY (equipment_id)
        REFERENCES equipment (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_issued_tx_id ON transaction_issued_items (transaction_id);
CREATE INDEX idx_issued_eq_id ON transaction_issued_items (equipment_id);

-- 5. RETURNED ITEMS TABLE (Part C Mapping)
CREATE TABLE transaction_returned_items
(
    id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    transaction_id BIGINT UNSIGNED                              NOT NULL,
    equipment_id   BIGINT UNSIGNED                              NOT NULL,
    item_condition ENUM ('GOOD', 'FAIR', 'DAMAGED', 'OBSOLETE') NOT NULL DEFAULT 'GOOD',
    remarks        TEXT                                                  DEFAULT NULL,
    created_at     TIMESTAMP                                             DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_returned_tx FOREIGN KEY (transaction_id)
        REFERENCES equipment_transactions (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_returned_eq FOREIGN KEY (equipment_id)
        REFERENCES equipment (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_returned_tx_id ON transaction_returned_items (transaction_id);
CREATE INDEX idx_returned_eq_id ON transaction_returned_items (equipment_id);

-- 6. ICT CHECKLIST TABLE (Part E Mapping - Directorate of ICT Use Only)
CREATE TABLE ict_checklists
(
    id                   BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    transaction_id       BIGINT UNSIGNED NOT NULL,
    os_installed         VARCHAR(80)              DEFAULT NULL,
    app_system_installed VARCHAR(120)             DEFAULT NULL,
    anti_virus_installed VARCHAR(80)              DEFAULT NULL,
    pdf_reader_installed VARCHAR(80)              DEFAULT NULL,
    is_joined_to_domain  BOOLEAN         NOT NULL DEFAULT FALSE,
    is_installed_vpn     BOOLEAN         NOT NULL DEFAULT FALSE,
    is_installed_printer BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at           TIMESTAMP                DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_checklist_tx_id UNIQUE (transaction_id),
    CONSTRAINT fk_checklist_tx FOREIGN KEY (transaction_id)
        REFERENCES equipment_transactions (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;