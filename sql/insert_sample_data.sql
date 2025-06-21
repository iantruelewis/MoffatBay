  USE moffat_bay_lodge;

-- Users
INSERT INTO user (name, email, phone, password, google_id, comments) VALUES
  ('David Reynolds', 'dreynolds@example.com', '555-123-4567', '24D9C33B4C17AB9A34E6A818ABC1FBE4-30123A2ED044DA0C2835938DAAD04C1078DA24D638655781BD2242A26B26311B91EA39147BF70865686699AA8CAA3BACEFA5F0C6D9B3BE1BEB2CA24BB24FF24F', NULL, 'First-time Guest.'),
  ('Gregg Mitchell', 'gmitchell@example.com', '555-234-5678', '24D9C33B4C17AB9A34E6A818ABC1FBE4-30123A2ED044DA0C2835938DAAD04C1078DA24D638655781BD2242A26B26311B91EA39147BF70865686699AA8CAA3BACEFA5F0C6D9B3BE1BEB2CA24BB24FF24F', 'google123', 'Corporate Retreat.'),
  ('Suzie Clementine', 'sclementine@example.com', '555-345-6789', '24D9C33B4C17AB9A34E6A818ABC1FBE4-30123A2ED044DA0C2835938DAAD04C1078DA24D638655781BD2242A26B26311B91EA39147BF70865686699AA8CAA3BACEFA5F0C6D9B3BE1BEB2CA24BB24FF24F', NULL, 'Wedding Party.');

-- Reservations
INSERT INTO reservation (uid, checkin, checkout, guest_count, comment) VALUES
  (1, '2025-07-01', '2025-07-03', 2, 'Family vacation'),
  (2, '2025-08-05', '2025-08-09', 1, 'Business retreat'),
  (3, '2025-09-10', '2025-09-15', 4, 'Wedding party');

-- Room Inventory
INSERT INTO room_inventory (room_num, type) VALUES
  (101, '1king'),
  (102, '2queen'),
  (201, '1queen'),
  (202, '2full');

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