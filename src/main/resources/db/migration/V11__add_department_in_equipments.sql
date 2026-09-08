alter table equipment
    add department enum ('ICT', 'FINANCE_AND_ACCOUNTS', 'LEGAL_SERVICES', 'HUMAN_RESOURCE_AND_ADMINISTRATION', 'PLANNING_AND_COORDINATION')  null after serial_number;

