alter table equipment_requests
    modify transaction_id bigint null after request_type;

alter table equipment
    modify description varchar(255) null after brand_model;

alter table equipment_transactions
    modify employee_signed tinyint(1) default 0 null after staff_id;

alter table equipment_transactions
    modify officer_signed tinyint(1) default 0 null after issuing_officer_id;

alter table users
    modify email varchar(100) not null after employee_id;

alter table users
    modify status enum ('active', 'suspended', 'inactive', 'pending') default 'active' null after mobile_no;

