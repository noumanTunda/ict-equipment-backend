-- Add boolean signed fields to equipment_transactions table
ALTER TABLE equipment_transactions
ADD COLUMN employee_signed BOOLEAN DEFAULT FALSE;

ALTER TABLE equipment_transactions
ADD COLUMN officer_signed BOOLEAN DEFAULT FALSE;
