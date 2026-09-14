-- Add keyphrase field to users table for transaction signing
ALTER TABLE users
ADD COLUMN keyphrase VARCHAR(255) NULL;
