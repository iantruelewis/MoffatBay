-- Initialize Moffat Bay Lodge Database and user
-- -------------------------------------------------
-- Caution! This will drop the existing database! --
-- -------------------------------------------------
-- Run as ROOT
-- Run: source create_db.sql

-- Drop then recreate database
DROP DATABASE moffat_bay_lodge;
CREATE DATABASE moffat_bay_lodge;

-- Drop user
DROP USER IF EXISTS 'mblodge'@'localhost';

-- Create user with a development password
CREATE USER 'mblodge'@'localhost' IDENTIFIED BY '4Yoi60UhMmZzGXaGRrlSJ2WDEYih3d99tEiC4PkYgAZlyA42s8hOIEZRNxJoI9N';

-- Grant privileges
GRANT DELETE, EXECUTE, INSERT, SELECT, SHOW VIEW, UPDATE ON moffat_bay_lodge.* TO 'mblodge'@'localhost';