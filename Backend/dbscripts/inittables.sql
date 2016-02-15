#Create tables

USE fbla2016;

CREATE TABLE users
(
	userid int unsigned not null auto_increment primary key,
	username char(50) not null,
	password char(64) not null,
	salt char(64) not null
);

CREATE TABLE auth
(
	sid int unsigned not null auto_increment primary key,
	userid int unsigned not null,
	authcode char(64),
	expire DATETIME
);

CREATE TABLE pictures
(
	pid int unsigned not null auto_increment primary key,
	userid int unsigned not null,
	title varchar(80) not null,
	geolat DECIMAL(4,2) not null,
	geolong DECIMAL(4,2) not null,
	created DATETIME not null,
	likes int unsigned not null,
	dislikes int unsigned not null,
	data MEDIUMBLOB not null
);

CREATE TABLE comments
(
	cid int unsigned not null auto_increment primary key,
	pid int unsigned not null,
	userid int unsigned not null,
	comment varchar(800),
	style int unsigned not null
);
