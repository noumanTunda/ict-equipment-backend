-- Add warranty fields to equipment table
ALTER TABLE equipment
ADD COLUMN has_warranty BOOLEAN DEFAULT FALSE NOT NULL AFTER supplier_details,
ADD COLUMN warranty_duration_months INT NULL AFTER has_warranty;

-- Add check constraint to ensure warranty_duration_months is not null when has_warranty is true
ALTER TABLE equipment
ADD CONSTRAINT chk_equipment_warranty_co_dependency
CHECK (
    (has_warranty = FALSE AND warranty_duration_months IS NULL) OR
    (has_warranty = TRUE AND warranty_duration_months IS NOT NULL AND warranty_duration_months >= 0)
);

-- Add index for warranty-related queries
CREATE INDEX idx_equipment_warranty ON equipment (has_warranty, warranty_duration_months);
