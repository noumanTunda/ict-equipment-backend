INSERT INTO equipment
(asset_number, serial_number, department, equipment_type, brand_model,
 supplier_details, status, description)
VALUES
-- 01
('AST-2026-001', 'SN 100000001', 'ICT', 'LAPTOP', 'HP ProBook 450 G9',
 'Techline Tanzania Ltd', 'AVAILABLE',
 'Business laptop for office and administrative use'),

-- 02
('AST-2026-002', 'SN 100000002', 'FINANCE_AND_ACCOUNTS', 'LAPTOP', 'Dell Latitude 5420',
 'Compulynx Tanzania Ltd', 'AVAILABLE',
 'Business laptop for staff use'),

-- 03
('AST-2026-003', 'SN 100000003', 'LEGAL_SERVICES', 'LAPTOP', 'Lenovo ThinkPad E14 Gen 4',
 'Alphatech Tanzania Ltd', 'AVAILABLE',
 'Business laptop for general office operations'),

-- 04
('AST-2026-004', 'SN 100000004', 'HUMAN_RESOURCE_AND_ADMINISTRATION', 'LAPTOP', 'HP EliteBook 840 G8',
 'Techline Tanzania Ltd', 'AVAILABLE',
 'Professional laptop for administrative work'),

-- 05
('AST-2026-005', 'SN 100000005', 'PLANNING_AND_COORDINATION', 'LAPTOP', 'Dell Latitude 5520',
 'Compulynx Tanzania Ltd', 'AVAILABLE',
 'Business laptop for departmental use'),

-- 06
('AST-2026-006', 'SN 100000006', 'ICT', 'LAPTOP', 'Lenovo ThinkPad L15 Gen 3',
 'Alphatech Tanzania Ltd', 'AVAILABLE',
 'Laptop for software and office applications'),

-- 07
('AST-2026-007', 'SN 100000007', 'FINANCE_AND_ACCOUNTS', 'LAPTOP', 'HP ProBook 440 G9',
 'Tronic Tanzania Ltd', 'AVAILABLE',
 'Portable computer for staff use'),

-- 08
('AST-2026-008', 'SN 100000008', 'LEGAL_SERVICES', 'LAPTOP', 'Dell Latitude 3420',
 'Compulynx Tanzania Ltd', 'AVAILABLE',
 'Entry-level business laptop'),

-- 09
('AST-2026-009', 'SN 100000009', 'HUMAN_RESOURCE_AND_ADMINISTRATION', 'LAPTOP', 'HP ProBook 450 G8',
 'Techline Tanzania Ltd', 'AVAILABLE',
 'Business laptop for general productivity'),

-- 10
('AST-2026-010', 'SN 100000010', 'PLANNING_AND_COORDINATION', 'LAPTOP', 'Lenovo ThinkPad E15 Gen 3',
 'Alphatech Tanzania Ltd', 'AVAILABLE',
 'Business laptop for departmental use'),

-- 11
('AST-2026-011', 'SN 100000011', 'ICT', 'DESKTOP', 'Dell OptiPlex 7090',
 'Compulynx Tanzania Ltd', 'AVAILABLE',
 'Desktop workstation for office operations'),

-- 12
('AST-2026-012', 'SN 100000012', 'FINANCE_AND_ACCOUNTS', 'DESKTOP', 'HP ProDesk 600 G6',
 'Techline Tanzania Ltd', 'AVAILABLE',
 'Desktop computer for administrative work'),

-- 13
('AST-2026-013', 'SN 100000013', 'LEGAL_SERVICES', 'DESKTOP', 'Lenovo ThinkCentre M720',
 'Alphatech Tanzania Ltd', 'AVAILABLE',
 'Desktop workstation for office applications'),

-- 14
('AST-2026-014', 'SN 100000014', 'HUMAN_RESOURCE_AND_ADMINISTRATION', 'DESKTOP', 'Dell OptiPlex 5080',
 'Compulynx Tanzania Ltd', 'AVAILABLE',
 'Office desktop workstation'),

-- 15
('AST-2026-015', 'SN 100000015', 'PLANNING_AND_COORDINATION', 'DESKTOP', 'HP ProDesk 400 G7',
 'Tronic Tanzania Ltd', 'AVAILABLE',
 'Desktop computer for general office use'),

-- 16
('AST-2026-016', 'SN 100000016', 'ICT', 'DESKTOP', 'Lenovo ThinkCentre M80q',
 'Alphatech Tanzania Ltd', 'AVAILABLE',
 'Compact desktop workstation'),

-- 17
('AST-2026-017', 'SN 100000017', 'FINANCE_AND_ACCOUNTS', 'PRINTER', 'HP LaserJet Pro M404dn',
 'Techline Tanzania Ltd', 'AVAILABLE',
 'Network laser printer for office printing'),

-- 18
('AST-2026-018', 'SN 100000018', 'LEGAL_SERVICES', 'PRINTER', 'Canon imageCLASS LBP6030',
 'Tronic Tanzania Ltd', 'AVAILABLE',
 'Monochrome laser printer'),

-- 19
('AST-2026-019', 'SN 100000019', 'HUMAN_RESOURCE_AND_ADMINISTRATION', 'PRINTER', 'Epson EcoTank L3250',
 'Alphatech Tanzania Ltd', 'AVAILABLE',
 'Colour multifunction ink tank printer'),

-- 20
('AST-2026-020', 'SN 100000020', 'PLANNING_AND_COORDINATION', 'PRINTER', 'Brother HL-L2350DW',
 'Compulynx Tanzania Ltd', 'AVAILABLE',
 'Wireless monochrome laser printer'),

-- 21
('AST-2026-021', 'SN 100000021', 'ICT', 'PRINTER', 'HP LaserJet Pro MFP M428fdw',
 'Techline Tanzania Ltd', 'AVAILABLE',
 'Multifunction network printer'),

-- 22
('AST-2026-022', 'SN 100000022', 'FINANCE_AND_ACCOUNTS', 'UPS', 'APC Back-UPS 650VA',
 'Tronic Tanzania Ltd', 'AVAILABLE',
 'Backup power supply for computer equipment'),

-- 23
('AST-2026-023', 'SN 100000023', 'LEGAL_SERVICES', 'UPS', 'APC Easy UPS 1200VA',
 'Techline Tanzania Ltd', 'AVAILABLE',
 'High-capacity backup power unit'),

-- 24
('AST-2026-024', 'SN 100000024', 'HUMAN_RESOURCE_AND_ADMINISTRATION', 'UPS', 'Eaton 5E 850VA',
 'Compulynx Tanzania Ltd', 'AVAILABLE',
 'UPS for workstation protection'),

-- 25
('AST-2026-025', 'SN 100000025', 'PLANNING_AND_COORDINATION', 'UPS', 'CyberPower UT650EG',
 'Alphatech Tanzania Ltd', 'AVAILABLE',
 'Backup power unit for desktop computers'),

-- 26
('AST-2026-026', 'SN 100000026', 'ICT', 'UPS', 'APC Back-UPS 1100VA',
 'Tronic Tanzania Ltd', 'AVAILABLE',
 'Power backup for network and computer equipment'),

-- 27
('AST-2026-027', 'SN 100000027', 'FINANCE_AND_ACCOUNTS', 'SCANNER', 'Canon imageFORMULA DR-C225 II',
 'Techline Tanzania Ltd', 'AVAILABLE',
 'Document scanner for office records'),

-- 28
('AST-2026-028', 'SN 100000028', 'LEGAL_SERVICES', 'SCANNER', 'Epson WorkForce ES-50',
 'Alphatech Tanzania Ltd', 'AVAILABLE',
 'Portable document scanner'),

-- 29
('AST-2026-029', 'SN 100000029', 'HUMAN_RESOURCE_AND_ADMINISTRATION', 'SCANNER', 'HP ScanJet Pro 2500 f1',
 'Compulynx Tanzania Ltd', 'AVAILABLE',
 'Flatbed document scanner'),

-- 30
('AST-2026-030', 'SN 100000030', 'PLANNING_AND_COORDINATION', 'SCANNER', 'Canon CanoScan LiDE 400',
 'Tronic Tanzania Ltd', 'AVAILABLE',
 'Flatbed colour document scanner'),

-- 31
('AST-2026-031', 'SN 100000031', 'ICT', 'MONITOR', 'Dell P2422H 24-inch',
 'Compulynx Tanzania Ltd', 'AVAILABLE',
 '24-inch Full HD office monitor'),

-- 32
('AST-2026-032', 'SN 100000032', 'FINANCE_AND_ACCOUNTS', 'MONITOR', 'HP E24 G4 24-inch',
 'Techline Tanzania Ltd', 'AVAILABLE',
 '24-inch business monitor'),

-- 33
('AST-2026-033', 'SN 100000033', 'LEGAL_SERVICES', 'MONITOR', 'Lenovo ThinkVision T24i-20',
 'Alphatech Tanzania Ltd', 'AVAILABLE',
 '24-inch professional monitor'),

-- 34
('AST-2026-034', 'SN 100000034', 'HUMAN_RESOURCE_AND_ADMINISTRATION', 'MONITOR', 'Dell P2422HE 24-inch',
 'Compulynx Tanzania Ltd', 'AVAILABLE',
 '24-inch USB-C business monitor'),

-- 35
('AST-2026-035', 'SN 100000035', 'PLANNING_AND_COORDINATION', 'MONITOR', 'Samsung S24R350 24-inch',
 'Tronic Tanzania Ltd', 'AVAILABLE',
 '24-inch Full HD LED monitor'),

-- 36
('AST-2026-036', 'SN 100000036', 'ICT', 'MONITOR', 'LG 24MP400-B 24-inch',
 'Alphatech Tanzania Ltd', 'AVAILABLE',
 '24-inch Full HD monitor'),

-- 37
('AST-2026-037', 'SN 100000037', 'FINANCE_AND_ACCOUNTS', 'OTHER', 'TP-Link 24-Port Gigabit Switch',
 'Techline Tanzania Ltd', 'AVAILABLE',
 'Network switch for office connectivity'),

-- 38
('AST-2026-038', 'SN 100000038', 'LEGAL_SERVICES', 'OTHER', 'Ubiquiti UniFi Access Point',
 'Tronic Tanzania Ltd', 'AVAILABLE',
 'Wireless network access point'),

-- 39
('AST-2026-039', 'SN 100000039', 'HUMAN_RESOURCE_AND_ADMINISTRATION', 'OTHER', 'Logitech MK270 Wireless Keyboard and Mouse',
 'Compulynx Tanzania Ltd', 'AVAILABLE',
 'Wireless keyboard and mouse set'),

-- 40
('AST-2026-040', 'SN 100000040', 'PLANNING_AND_COORDINATION', 'OTHER', 'Epson EB-E01 Projector',
 'Techline Tanzania Ltd', 'AVAILABLE',
 'Multimedia projector for presentations');