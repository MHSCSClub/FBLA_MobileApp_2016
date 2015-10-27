CREATE DATABASE fbla2016;
CREATE USER 'admin'@'localhost' IDENTIFIED BY 'changeme';
GRANT ALL PRIVILEGES on *.* TO 'admin'@'localhost' WITH GRANT OPTION;
CREATE USER 'fblaadmin'@'localhost' IDENTIFIED BY '123456';
GRANT SELECT,INSERT,UPDATE,DELETE,CREATE ON fbla2016.* TO 'fblaadmin'@'localhost';
