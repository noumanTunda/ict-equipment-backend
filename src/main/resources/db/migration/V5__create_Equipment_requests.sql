CREATE TABLE equipment_requests
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    request_code     VARCHAR(50) NOT NULL,
    staff_id         BIGINT      NOT NULL,
    request_type     VARCHAR(20) NOT NULL,
    reason           VARCHAR(500) NULL,
    return_asset_number VARCHAR(50) NULL,
    issue_asset_number VARCHAR(50) NULL,
    preferred_equipment_type VARCHAR(100) NULL,
    status           VARCHAR(20) NOT NULL,
    rejection_reason VARCHAR(500) NULL,
    approved_by      BIGINT NULL,
    approved_at      datetime(6)           NULL,
    created_at       datetime(6)           NULL,
    updated_at       datetime(6)           NULL,
    transaction_id   BIGINT NULL,
    CONSTRAINT pk_equipment_requests PRIMARY KEY (id)
);

ALTER TABLE equipment_requests
    ADD CONSTRAINT uc_equipment_requests_request_code UNIQUE (request_code);