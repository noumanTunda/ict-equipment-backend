alter table users
    add status ENUM('active', 'suspended', 'inactive', 'pending') DEFAULT 'active';

alter table users
    change position role varchar(80) not null;
    modify role ENUM('ROLE_STAFF', 'ROLE_ICT_OFFICER', 'ROLE_ADMIN') NOT NULL DEFAULT 'ROLE_STAFF';
