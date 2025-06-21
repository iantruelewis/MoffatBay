DROP DATABASE IF EXISTS moffat_bay_lodge;
CREATE DATABASE moffat_bay_lodge;
USE moffat_bay_lodge;

-- Users table
CREATE TABLE user (
    uid INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    password VARCHAR(256) NOT NULL,
    google_id VARCHAR(100),
    comments TEXT
);

-- Reservations table
CREATE TABLE reservation (
    res_id INT AUTO_INCREMENT PRIMARY KEY,
    uid INT NOT NULL,
    checkin DATE NOT NULL,
    checkout DATE NOT NULL,
    guest_count INT NOT NULL,
    comment TEXT,
    FOREIGN KEY (uid) REFERENCES user(uid)
);

-- RoomInventory table
CREATE TABLE room_inventory (
    room_num INT PRIMARY KEY,
    type VARCHAR(20) NOT NULL
    -- Consider: values like '1queen', '1king', '2full', '2queen'
);

-- RoomReservation table (junction table)
CREATE TABLE room_reservation (
    res_id INT NOT NULL,
    room_num INT NOT NULL,
    PRIMARY KEY (res_id, room_num),
    FOREIGN KEY (res_id) REFERENCES reservation(res_id),
    FOREIGN KEY (room_num) REFERENCES room_inventory(room_num)
);

-- Newsletter table
CREATE TABLE newsletter (
    email VARCHAR(100) PRIMARY KEY,
    active BOOLEAN NOT NULL
);

-- ContactForm table
CREATE TABLE contact_form (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    message TEXT,
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN NOT NULL DEFAULT FALSE
);