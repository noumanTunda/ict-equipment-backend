ALTER TABLE equipment_transactions
    MODIFY COLUMN employee_signature TINYTEXT NULL;

ALTER TABLE equipment_transactions
    MODIFY COLUMN officer_signature TINYTEXT NULL;
