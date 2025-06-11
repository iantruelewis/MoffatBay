/* 
Project: Moffat Bay Lodge
Team: Team 3
Team Members: Ian Lewis, Robert Minkler, Kevin Ramirez
Date: June 11, 2025

*/

DROP TABLE IF EXISTS contact_form;
DROP TABLE IF EXISTS newsletter;
DROP TABLE IF EXISTS room_reservation;
DROP TABLE IF EXISTS room_inventory;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS user;

-- Create tables
CREATE TABLE user (
    uid INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    password VARCHAR(256) NOT NULL,
    google_id VARCHAR(100),
    comments TINYTEXT
);

CREATE TABLE reservation (
    res_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    uid INT NOT NULL,
    checkin DATE NOT NULL,
    checkout DATE NOT NULL,
    guest_count INT NOT NULL,
    comment TINYTEXT,
    FOREIGN KEY (uid) REFERENCES user(uid)
);

CREATE TABLE room_inventory (
    room_num INT NOT NULL PRIMARY KEY,
    type ENUM('1queen', '1king', '2full', '2queen') NOT NULL
);

CREATE TABLE room_reservation (
    res_id INT NOT NULL,
    room_num INT NOT NULL,
    PRIMARY KEY (res_id, room_num),
    FOREIGN KEY (res_id) REFERENCES reservation(res_id),
    FOREIGN KEY (room_num) REFERENCES room_inventory(room_num)
);

CREATE TABLE newsletter (
    email VARCHAR(100) NOT NULL PRIMARY KEY,
    active BIT NOT NULL
);

CREATE TABLE contact_form (
    id INT NOT NULL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    message TINYTEXT,
    timestamp TIMESTAMP NOT NULL,
    is_read BIT NOT NULL
);

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
