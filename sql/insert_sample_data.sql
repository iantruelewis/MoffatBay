  USE moffat_bay_lodge;

-- Users
INSERT INTO user (name, email, phone, password, google_id, comments) VALUES
  ('David Reynolds', 'dreynolds@example.com', '555-123-4567', '3718FA76F2F3D9763613EB53A54EA497-AB84AF14C7785EF7AF02129D7943FC0D3D64FCCB325929B0BECA673B48A78A8620544CFFE1A6868C6084FCBE42D8C6C88D3BDEC7EE3D8005B292232AB7412A15', NULL, 'First-time Guest.'),
  ('Gregg Mitchell', 'gmitchell@example.com', '555-234-5678', '5799887C8F60CF521A73DB8CBC3CCD70-59F3B0462BB93EC0469A51CF17EF4058B91F4E106E125EC27755D32CDFB3199F4CC6D652CB2E4D4BFEEDFC571DCC7B855366144406B6DE1F135DA0142A1EFD0C', 'google123', 'Corporate Retreat.'),
  ('Suzie Clementine', 'sclementine@example.com', '555-345-6789', 'A44BCA401E9FA14AF9A5F1CF7A85E6E3-259F87EC2F86336991A12661AE9E8323304EDCDB3DBCE99AA9359FCD3050C444B0679A2B5D103F4BE79F3938AB29F26DC83EB1BDAF01098EDEE2DEF998E15992', NULL, 'Wedding Party.');

-- Reservations
INSERT INTO reservation (uid, checkin, checkout, guest_count, comment) VALUES
  (1, '2025-07-01', '2025-07-03', 2, 'Family vacation'),
  (2, '2025-08-05', '2025-08-09', 1, 'Business retreat'),
  (3, '2025-09-10', '2025-09-15', 4, 'Wedding party');

-- Room Inventory
INSERT INTO room_inventory (room_num, type) VALUES
  (101, '1king'),
  (102, '2queen'),
  (103, '1king'),
  (104, '1king'),
  (105, '1king'),
  (106, '2queen'),
  (107, '2queen'),
  (108, '2queen'),
  (109, '2queen'),
  (201, '1queen'),
  (202, '2full'),
  (203, '1queen'),
  (204, '1queen'),
  (205, '1queen'),
  (206, '2full'),
  (207, '2full'),
  (208, '2full'),
  (209, '2full');


-- Room Reservation
INSERT INTO room_reservation (res_id, room_num) VALUES
  (1, 101),
  (2, 201),
  (3, 102),
  (3, 202);

-- Newsletter
INSERT INTO newsletter (email, active) VALUES
  ('dreynolds@example.com', TRUE),
  ('gmitchell@example.com', TRUE),
  ('sclementine@example.com', FALSE);

-- Contact Form
INSERT INTO contact_form (name, email, message, is_read) VALUES
  ('Janet Fritz', 'jfritz@example.com', 'Interested in marina.', FALSE),
  ('Alice Dermitt', 'adermitt@example.com', 'Do you allow pets?', TRUE),
  ('Maria Lopez', 'mlopez@example.com', 'Special dietary requests for lodge meals.', FALSE);
