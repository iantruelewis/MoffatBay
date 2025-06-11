/* 
Project: Moffat Bay Lodge
Team: Team 3
Team Members: Ian Lewis, Robert Minkler, Kevin Ramirez
Date: June 11, 2025

*/

-- Insert Sample Data
-- Run: source sql/insert_data.sql

USE moffat_bay_lodge;

-- Insert sample data (TDD-based)
INSERT INTO user (name, email, phone, password, google_id, comments) VALUES
('David Reynolds', 'david.reynolds@example.com', '555-1111', 'hash_david_pw', 'google_david123', 'Prefers quick mobile access'),
('Gregg Mitchell', 'gregg.mitchell@example.com', '555-2222', 'hash_gregg_pw', NULL, 'Booking for team retreat'),
('Suzie Clementine', 'suzie.clementine@example.com', '555-3333', 'hash_suzie_pw', NULL, 'Wedding planning at the bay');

INSERT INTO reservation (uid, checkin, checkout, guest_count, comment) VALUES
(1, '2025-07-10', '2025-07-15', 4, 'Family vacation and fishing'),
(2, '2025-08-01', '2025-08-05', 8, 'Retreat with sales team'),
(3, '2025-06-20', '2025-06-23', 12, 'Wedding and reception reservation');

INSERT INTO room_inventory (room_num, type) VALUES
(101, '1queen'),
(102, '2full'),
(103, '1king');

INSERT INTO room_reservation (res_id, room_num) VALUES
(1, 101),
(2, 102),
(3, 103);

INSERT INTO newsletter (email, active) VALUES
('david.reynolds@example.com', b'1'),
('gregg.mitchell@example.com', b'0'),
('suzie.clementine@example.com', b'1');

INSERT INTO contact_form (id, name, email, message, timestamp, is_read) VALUES
(1, 'David Reynolds', 'david.reynolds@example.com', 'Are there any activities for kids?', CURRENT_TIMESTAMP, b'0'),
(2, 'Gregg Mitchell', 'gregg.mitchell@example.com', 'Is there a projector for presentations?', CURRENT_TIMESTAMP, b'1'),
(3, 'Suzie Clementine', 'suzie.clementine@example.com', 'Can I tour the outdoor wedding space?', CURRENT_TIMESTAMP, b'0');
