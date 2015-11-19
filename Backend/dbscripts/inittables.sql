#Create tables

CREATE TABLE users
(
	userid int unsigned not null auto_increment primary key,
	username char(50) not null,
	password char(64) not null,
  salt char(64) not null,
);

CREATE TABLE auth
(
	sid int unsigned not null auto_increment primary key,
	userid int unsigned not null,
	authcode char(64),
	expire DATETIME
);
