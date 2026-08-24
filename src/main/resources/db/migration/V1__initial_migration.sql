create table equipment
(
    id               bigint unsigned auto_increment
        primary key,
    asset_number     varchar(60)                                                                                     not null,
    serial_number    varchar(60)                                                                                     not null,
    equipment_type   enum ('LAPTOP', 'DESKTOP', 'PRINTER', 'UPS', 'SCANNER', 'MONITOR', 'OTHER')                     not null,
    brand_model      varchar(100)                                                                                    not null,
    supplier_details varchar(255)                                                                                    null,
    status           enum ('AVAILABLE', 'ISSUED', 'RETURNED', 'MAINTENANCE', 'DISPOSED') default 'AVAILABLE'         not null,
    created_at       timestamp                                                           default current_timestamp() null,
    updated_at       timestamp                                                           default current_timestamp() null on update current_timestamp(),
    constraint uq_equipment_asset_number
        unique (asset_number),
    constraint uq_equipment_serial_number
        unique (serial_number)
)
    collate = utf8mb4_unicode_ci;

create index idx_equipment_status
    on equipment (status);

create index idx_equipment_type
    on equipment (equipment_type);

create table users
(
    id          bigint unsigned auto_increment
        primary key,
    employee_id varchar(30)                                                                     not null,
    full_name   varchar(120)                                                                    not null,
    password    varchar(255)                                                                    not null,
    department  varchar(80)                                                                     not null,
    role        varchar(80)                                                                     not null,
    mobile_no   varchar(20)                                                                     not null,
    email       varchar(100)                                                                    not null,
    created_at  timestamp                                           default current_timestamp() null,
    updated_at  timestamp                                           default current_timestamp() null on update current_timestamp(),
    status      enum ('active', 'suspended', 'inactive', 'pending') default 'active'            null,
    constraint uq_users_email
        unique (email),
    constraint uq_users_employee_id
        unique (employee_id)
)
    collate = utf8mb4_unicode_ci;

create table equipment_transactions
(
    id                 bigint unsigned auto_increment
        primary key,
    transaction_code   varchar(36)                                                                               not null,
    staff_id           bigint unsigned                                                                           not null,
    issuing_officer_id bigint unsigned                                                                           not null,
    status             enum ('DRAFT', 'PENDING_SIGNATURE', 'COMPLETED', 'CANCELLED') default 'DRAFT'             not null,
    employee_signature mediumtext                                                                                null,
    employee_signed_at datetime                                                                                  null,
    officer_signature  mediumtext                                                                                null,
    officer_signed_at  datetime                                                                                  null,
    created_at         timestamp                                                     default current_timestamp() null,
    updated_at         timestamp                                                     default current_timestamp() null on update current_timestamp(),
    constraint uq_tx_code
        unique (transaction_code),
    constraint fk_tx_officer
        foreign key (issuing_officer_id) references users (id)
            on update cascade,
    constraint fk_tx_staff
        foreign key (staff_id) references users (id)
            on update cascade
)
    collate = utf8mb4_unicode_ci;

create index idx_tx_created_at
    on equipment_transactions (created_at);

create index idx_tx_officer_id
    on equipment_transactions (issuing_officer_id);

create index idx_tx_staff_id
    on equipment_transactions (staff_id);

create index idx_tx_status
    on equipment_transactions (status);

create table ict_checklists
(
    id                   bigint unsigned auto_increment
        primary key,
    transaction_id       bigint unsigned                        not null,
    os_installed         varchar(80)                            null,
    app_system_installed varchar(120)                           null,
    anti_virus_installed varchar(80)                            null,
    pdf_reader_installed varchar(80)                            null,
    is_joined_to_domain  tinyint(1) default 0                   not null,
    is_installed_vpn     tinyint(1) default 0                   not null,
    is_installed_printer tinyint(1) default 0                   not null,
    created_at           timestamp  default current_timestamp() null,
    updated_at           timestamp  default current_timestamp() null on update current_timestamp(),
    constraint uq_checklist_tx_id
        unique (transaction_id),
    constraint fk_checklist_tx
        foreign key (transaction_id) references equipment_transactions (id)
            on update cascade on delete cascade
)
    collate = utf8mb4_unicode_ci;

create table transaction_issued_items
(
    id                   bigint unsigned auto_increment
        primary key,
    transaction_id       bigint unsigned                       not null,
    equipment_id         bigint unsigned                       not null,
    accessories_provided varchar(500)                          null,
    created_at           timestamp default current_timestamp() null,
    updated_at           datetime(6)                           null,
    constraint fk_issued_eq
        foreign key (equipment_id) references equipment (id)
            on update cascade,
    constraint fk_issued_tx
        foreign key (transaction_id) references equipment_transactions (id)
            on update cascade on delete cascade
)
    collate = utf8mb4_unicode_ci;

create index idx_issued_eq_id
    on transaction_issued_items (equipment_id);

create index idx_issued_tx_id
    on transaction_issued_items (transaction_id);

create table transaction_returned_items
(
    id             bigint unsigned auto_increment
        primary key,
    transaction_id bigint unsigned                                                          not null,
    equipment_id   bigint unsigned                                                          not null,
    item_condition enum ('GOOD', 'FAIR', 'DAMAGED', 'OBSOLETE') default 'GOOD'              not null,
    remarks        text                                                                     null,
    created_at     timestamp                                    default current_timestamp() null,
    constraint fk_returned_eq
        foreign key (equipment_id) references equipment (id)
            on update cascade,
    constraint fk_returned_tx
        foreign key (transaction_id) references equipment_transactions (id)
            on update cascade on delete cascade
)
    collate = utf8mb4_unicode_ci;

create index idx_returned_eq_id
    on transaction_returned_items (equipment_id);

create index idx_returned_tx_id
    on transaction_returned_items (transaction_id);

create index idx_users_department
    on users (department);

create index idx_users_full_name
    on users (full_name);

