-- Add nullable return_equipment_id column to equipment_requests table
ALTER TABLE equipment_requests 
ADD COLUMN return_equipment_id BIGINT(20) UNSIGNED DEFAULT NULL,
ADD CONSTRAINT fk_equipment_requests_return_eq 
    FOREIGN KEY (return_equipment_id) REFERENCES equipment(id) 
    ON DELETE SET NULL ON UPDATE CASCADE;
