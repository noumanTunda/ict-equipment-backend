-- 1. Remove the trigger causing the collation error
DROP TRIGGER IF EXISTS generate_asset_and_serial_before_insert;

-- 2. Re-create with fixed collation settings
DELIMITER //

CREATE TRIGGER generate_asset_and_serial_before_insert
    BEFORE INSERT ON equipment
    FOR EACH ROW
BEGIN
    -- Declare current_year matching your table's exact collation
    DECLARE current_year VARCHAR(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    DECLARE max_ast_num INT DEFAULT 0;
    DECLARE max_sn_num BIGINT DEFAULT 99999999;

    SET current_year = DATE_FORMAT(NOW(), '%Y');

    -- 1. Generate Asset Number (AST-YYYY-NNN)
    IF NEW.asset_number IS NULL OR NEW.asset_number = '' THEN
    SELECT IFNULL(MAX(CAST(RIGHT(asset_number, 3) AS UNSIGNED)), 0)
    INTO max_ast_num
    FROM equipment
    WHERE asset_number LIKE CONCAT('AST-', current_year, '-%') COLLATE utf8mb4_unicode_ci;

    SET NEW.asset_number = CONCAT('AST-', current_year, '-', LPAD(max_ast_num + 1, 3, '0'));
END IF;

-- 2. Generate Serial Number (SN XXXXXXXXX)
IF NEW.serial_number IS NULL OR NEW.serial_number = '' THEN
SELECT IFNULL(MAX(CAST(SUBSTRING(serial_number, 4) AS UNSIGNED)), 99999999)
INTO max_sn_num
FROM equipment
WHERE serial_number LIKE 'SN %' COLLATE utf8mb4_unicode_ci;

SET NEW.serial_number = CONCAT('SN ', max_sn_num + 1);
END IF;
END;
//

DELIMITER ;