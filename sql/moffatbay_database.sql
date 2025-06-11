/* 
Project: Moffat Bay Lodge
Team: Team 3
Team Members: Ian Lewis, Robert Minkler, Kevin Ramirez
Date: June 11, 2025

*/

-- Initialize Moffat Bay Lodge Database Tables
-- -----------------------------------------------
-- Caution! This will drop the existing tables! --
-- -----------------------------------------------
-- Run as ROOT
-- Run: source sql/moffatbay_database.sql

USE moffat_bay_lodge;

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
