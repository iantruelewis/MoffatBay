-- Drop the user if it already exists
DROP USER IF EXISTS 'mblodge'@'localhost';

-- Create the user with the specified development password
CREATE USER 'mblodge'@'localhost'
IDENTIFIED BY '4Yoi60UhMmZzGXaGRrlSJ2WDEYih3d99tEiC4PkYgAZlyA42s8hOIEZRNxJoI9N';

-- Grant specific privileges on the moffat_bay_lodge database
GRANT DELETE, EXECUTE, INSERT, SELECT, SHOW VIEW, UPDATE
ON moffat_bay_lodge.* TO 'mblodge'@'localhost';

-- Apply changes
FLUSH PRIVILEGES;