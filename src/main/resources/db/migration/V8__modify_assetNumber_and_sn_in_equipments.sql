DELIMITER //

CREATE TRIGGER generate_asset_and_serial_before_insert
    BEFORE INSERT ON equipment
    FOR EACH ROW
BEGIN
    DECLARE current_year VARCHAR(4);
    DECLARE max_ast_num INT DEFAULT 0;
    DECLARE max_sn_num BIGINT DEFAULT 99999999; -- Baseline so the first record starts at 100000000

    -- Get the current 4-digit year
    SET current_year = DATE_FORMAT(NOW(), '%Y');

    -- 1. Generate Asset Number (AST-YYYY-NNN)
    SELECT IFNULL(MAX(CAST(RIGHT(asset_number, 3) AS UNSIGNED)), 0)
    INTO max_ast_num
    FROM equipment
    WHERE asset_number LIKE CONCAT('AST-', current_year, '-%');

    SET NEW.asset_number = CONCAT('AST-', current_year, '-', LPAD(max_ast_num + 1, 3, '0'));

    -- 2. Generate Serial Number (SN XXXXXXXXX starting at 100000000)
    SELECT IFNULL(MAX(CAST(SUBSTRING(serial_number, 4) AS UNSIGNED)), 99999999)
    INTO max_sn_num
    FROM equipment
    WHERE serial_number LIKE 'SN %';

    SET NEW.serial_number = CONCAT('SN ', max_sn_num + 1);
END;
//

DELIMITER ;