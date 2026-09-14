alter table equipment_requests
drop column return_asset_number;

alter table equipment_requests
drop column issue_asset_number;

alter table equipment_requests
    modify status ENUM ('PENDING', 'APPROVED', 'REJECTED', 'COMPLETED') not null;