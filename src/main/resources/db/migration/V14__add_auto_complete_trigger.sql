-- Enable the Event Scheduler if not already enabled
SET GLOBAL event_scheduler = ON;

-- Create an event that runs every 5 seconds to check for requests that should be COMPLETED
-- This handles the "after 1 second" requirement by checking periodically
CREATE EVENT IF NOT EXISTS auto_complete_equipment_requests
ON SCHEDULE EVERY 5 SECOND
DO
BEGIN
    -- Update APPROVED requests to COMPLETED if:rejection_reason is null, transaction_id is not null, corresponding transaction status is COMPLETED
    UPDATE equipment_requests er
    INNER JOIN equipment_transactions et ON er.transaction_id = et.id
    SET er.status = 'COMPLETED'
    WHERE er.status = 'APPROVED'
      AND er.rejection_reason IS NULL
      AND er.transaction_id IS NOT NULL
      AND et.status = 'COMPLETED'
      AND TIMESTAMPDIFF(SECOND, er.updated_at, NOW()) >= 1;
END;
