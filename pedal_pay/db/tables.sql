SET FOREIGN_KEY_CHECKS = 0; # set key checks to 0 so i can drop tables without forign key hassle -CR
DROP TABLE IF EXISTS accountreset;
DROP TABLE IF EXISTS bikeupdates;
DROP TABLE IF EXISTS booking;
DROP TABLE IF EXISTS bikes;
DROP TABLE IF EXISTS equipment;
DROP TABLE IF EXISTS locations;
DROP TABLE IF EXISTS city;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS review;
SET FOREIGN_KEY_CHECKS = 1;

#INSERT INTO users (email, password, isadmin) VALUES ('calum', 'calum', FALSE);
CREATE TABLE IF NOT EXISTS users(
	id INT AUTO_INCREMENT,
	email VARCHAR(25) NOT NULL UNIQUE,
	password VARCHAR(65) NOT NULL,
	name VARCHAR(25),
	gender ENUM('m','f'),
	mobile VARCHAR(20),
	dob DATE,
	addressone VARCHAR(65),
	addresstwo VARCHAR(65),
	city VARCHAR(20),
	zip VARCHAR(20),
	isadmin BOOLEAN,
	PRIMARY KEY (id)
)ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS city(
	id INT AUTO_INCREMENT,
	lat DECIMAL(11, 8) NOT NULL,
	lng DECIMAL(11, 8) NOT NULL,
	name VARCHAR(20) NOT NULL,
	PRIMARY KEY (id)
)ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS locations(
	id INT AUTO_INCREMENT,
	hub VARCHAR(20) NOT NULL,
	lat DECIMAL(11, 8) NOT NULL,
	lng DECIMAL(11, 8) NOT NULL,
	city INT NOT NULL,
	PRIMARY KEY (id)
)ENGINE=INNODB;

#INSERT INTO bikes VALUES ('CityBike', 1, 'wheel');
CREATE TABLE IF NOT EXISTS bikes(
	id INT AUTO_INCREMENT,
	biketype ENUM('CityBike', 'Mountain', 'Tandem', 'Handbike') NOT NULL,
	hub INT NOT NULL,
	damage ENUM('wheel', 'oil', 'unknown', 'wrecked'), # null value default for no damage
	PRIMARY KEY (id)
)ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS equipment(
	id INT AUTO_INCREMENT,
	item ENUM('helmet', 'pump', 'bag', 'highvisjacket') NOT NULL, # maybe add more items
	hub INT NOT NULL,
	returned BOOLEAN NOT NULL,
	PRIMARY KEY (id)
)ENGINE=INNODB;

#INSERT INTO booking (pid, bid, lid, bookstart, bookend, returned, code) VALUES (1, 1, 1 '2018-09-14 10:00:00', '2018-09-14 20:00:00', FALSE, 18732);
#UPDATE booking SET returned = TRUE WHERE pid = 1;
# pid is person id
CREATE TABLE IF NOT EXISTS booking( # possibly use big int instead of datetime
	id INT AUTO_INCREMENT,
	pid INT NOT NULL,
	bid INT NOT NULL,
 	lid INT NOT NULL,
	bookstart DATETIME NOT NULL,
	bookend DATETIME NOT NULL,
	returned BOOLEAN NOT NULL, 		# gets set to true if it is returned 0 is false
	processing BOOLEAN,
	code INT NOT NULL,			# for email and for unlocking the bike at the location
	distance FLOAT,
	PRIMARY KEY (id)
)ENGINE=INNODB;

#INSERT INTO bikeupdates (bid, lat, lng, time) VALUES (1, 55.913980, -3.161350, NOW());
CREATE TABLE IF NOT EXISTS bikeupdates(
	id INT AUTO_INCREMENT,
	bid INT NOT NULL,
	lat FLOAT NOT NULL,
	lng FLOAT NOT NULL,
	time TIMESTAMP, # might just store as a big int seconds from epoch
	PRIMARY KEY (id)
)ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS accountreset(
	fp_code VARCHAR(200) NOT NULL,
	timestamp BIGINT(20) NOT NULL,
	PRIMARY KEY (fp_code)
)ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS review(
	id INT AUTO_INCREMENT,
	pid int,
	rating INT NOT NULL,
	text VARCHAR(1020) NOT NULL,
	time TIMESTAMP,
	approved BOOLEAN,
	PRIMARY KEY (id)
)ENGINE=INNODB;



ALTER TABLE bikes ADD FOREIGN KEY (hub) REFERENCES locations(id);
ALTER TABLE equipment ADD FOREIGN KEY (hub) REFERENCES locations(id);
ALTER TABLE booking ADD FOREIGN KEY (bid) REFERENCES bikes(id);
ALTER TABLE booking ADD FOREIGN KEY (pid) REFERENCES users(id);
ALTER TABLE bikeupdates ADD FOREIGN KEY (bid) REFERENCES bikes(id);
ALTER TABLE locations ADD FOREIGN KEY (city) REFERENCES city(id);
ALTER TABLE users ADD INDEX(email);

# Delete Reset Session From Password Reset If Unused
DROP EVENT `deleteExpired`;
CREATE EVENT `deleteexpired` ON SCHEDULE EVERY 30 MINUTE STARTS '2019-01-01 00:00:00' ON COMPLETION NOT PRESERVE ENABLE DO DELETE FROM accountreset WHERE (SELECT UNIX_TIMESTAMP() - `timestamp` FROM (SELECT * FROM accountreset) AS rs) > 1800;

INSERT INTO city (lat, lng, name) VALUES (55.953251, -3.188267, 'Edinburgh');
INSERT INTO city (lat, lng, name) VALUES (51.509865, -0.118092, 'London');

INSERT INTO locations (hub, lat, lng, city) VALUES ('Haymarket', 55.94532700, -3.21830800, 1);
INSERT INTO locations (hub, lat, lng, city) VALUES ('Granton', 55.9827822, -3.2509026, 1);
INSERT INTO locations (hub, lat, lng, city) VALUES ('Waverly', 55.95200000, -3.19000000, 1);
INSERT INTO locations (hub, lat, lng, city) VALUES ('Sighthill', 55.9246, -3.2973243, 1);
INSERT INTO locations (hub, lat, lng, city) VALUES ('Leith', 55.97255000, -3.17094900, 1);
INSERT INTO locations (hub, lat, lng, city) VALUES ('Morningside', 55.92016300, -3.21631000, 1);
INSERT INTO locations (hub, lat, lng, city) VALUES ('Fort-Kinnard', 55.93228000, -3.10593000, 1);
INSERT INTO locations (hub, lat, lng, city) VALUES ('Edinburgh Zoo', 55.94220000, -3.26930000, 1);
INSERT INTO locations (hub, lat, lng, city) VALUES ('Holyrood', 55.94600000, -3.15960000, 1);
INSERT INTO locations (hub, lat, lng, city) VALUES ('Liberton', 55.91398000, -3.16135000, 1);
INSERT INTO locations (hub, lat, lng, city) VALUES ('Heriot-Watt University', 55.910291, -3.323458, 1);


INSERT INTO locations (hub, lat, lng, city) VALUES ('Tower of London', 51.508530, -0.076132, 2);
INSERT INTO locations (hub, lat, lng, city) VALUES ('London City Hall', 51.504790, -0.078709, 2);
INSERT INTO locations (hub, lat, lng, city) VALUES ('London Bridge', 51.507879, -0.087732, 2);
INSERT INTO locations (hub, lat, lng, city) VALUES ('Big Ben', 51.500729, -0.124625, 2);

#get all equipment from a location that is there
# SELECT * FROM equipment WHERE equipment.returned = TRUE AND equipment.hub = (SELECT id FROM locations WHERE locations.hub = 'Haymarket');

#5 chains per hub
INSERT INTO equipment (item, hub, returned) VALUES ('highvisjacket', 1, TRUE), ('highvisjacket', 1, TRUE), ('highvisjacket', 1, TRUE), ('highvisjacket', 1, TRUE), ('highvisjacket', 1, TRUE);
#10 bags
INSERT INTO equipment (item, hub, returned) VALUES ('bag', 1, TRUE), ('bag', 1, TRUE), ('bag', 1, TRUE), ('bag', 1, TRUE), ('bag', 1, TRUE), ('bag', 1, TRUE), ('bag', 1, TRUE), ('bag', 1, TRUE), ('bag', 1, TRUE), ('bag', 1, TRUE);
#5 pumps
INSERT INTO equipment (item, hub, returned) VALUES ('pump', 1, TRUE), ('pump', 1, TRUE), ('pump', 1, TRUE), ('pump', 1, TRUE), ('pump', 1, TRUE);
#~18 helmets as 18 bikes per location 20 at waverly though(this one)
INSERT INTO equipment (item, hub, returned) VALUES ('helmet', 1, TRUE), ('helmet', 1, TRUE), ('helmet', 1, TRUE), ('helmet', 1, TRUE), ('helmet', 1, TRUE), ('helmet', 1, TRUE), ('helmet', 1, TRUE), ('helmet', 1, TRUE), ('helmet', 1, TRUE), ('helmet', 1, TRUE), ('helmet', 1, TRUE), ('helmet', 1, TRUE), ('helmet', 1, TRUE), ('helmet', 1, TRUE), ('helmet', 1, TRUE), ('helmet', 1, TRUE), ('helmet', 1, TRUE), ('helmet', 1, TRUE), ('helmet', 1, TRUE), ('helmet', 1, TRUE);
#10 CityBike 4 Handbike 4 tandem 12 CityBike at this one though
INSERT INTO bikes (biketype, hub, damage) VALUES ('CityBike', 1, NULL), ('CityBike', 1, NULL), ('CityBike', 1, NULL), ('CityBike', 1, NULL), ('CityBike', 1, NULL), ('CityBike', 1, NULL), ('CityBike', 1, NULL), ('CityBike', 1, NULL), ('CityBike', 1, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Handbike', 1, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Tandem', 1, NULL), ('Tandem', 1, NULL), ('Tandem', 1, NULL), ('Tandem', 1, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Mountain', 1, NULL), ('Mountain', 1, NULL), ('Mountain', 1, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('CityBike', 1, 'wheel');
INSERT INTO bikes (biketype, hub, damage) VALUES ('CityBike', 1, 'oil');
INSERT INTO bikes (biketype, hub, damage) VALUES ('CityBike', 1, 'unknown');

#hub2
INSERT INTO equipment (item, hub, returned) VALUES ('highvisjacket', 2, TRUE), ('highvisjacket', 2, TRUE), ('highvisjacket', 2, TRUE), ('highvisjacket', 2, TRUE), ('highvisjacket', 2, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('bag', 2, TRUE), ('bag', 2, TRUE), ('bag', 2, TRUE), ('bag', 2, TRUE), ('bag', 2, TRUE), ('bag', 2, TRUE), ('bag', 2, TRUE), ('bag', 2, TRUE), ('bag', 2, TRUE), ('bag', 2, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('pump', 2, TRUE), ('pump', 2, TRUE), ('pump', 2, TRUE), ('pump', 2, TRUE), ('pump', 2, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('helmet', 2, TRUE), ('helmet', 2, TRUE), ('helmet', 2, TRUE), ('helmet', 2, TRUE), ('helmet', 2, TRUE), ('helmet', 2, TRUE), ('helmet', 2, TRUE), ('helmet', 2, TRUE), ('helmet', 2, TRUE), ('helmet', 2, TRUE), ('helmet', 2, TRUE), ('helmet', 2, TRUE), ('helmet', 2, TRUE), ('helmet', 2, TRUE), ('helmet', 2, TRUE), ('helmet', 2, TRUE), ('helmet', 2, TRUE), ('helmet', 2, TRUE);
INSERT INTO bikes (biketype, hub, damage) VALUES ('CityBike', 2, NULL), ('CityBike', 2, NULL), ('CityBike', 2, NULL), ('CityBike', 2, NULL), ('CityBike', 2, NULL), ('CityBike', 2, NULL), ('CityBike', 2, NULL), ('CityBike', 2, NULL), ('CityBike', 2, NULL), ('CityBike', 2, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Handbike', 2, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Tandem', 2, NULL), ('Tandem', 2, NULL), ('Tandem', 2, NULL), ('Tandem', 2, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Mountain', 2, NULL), ('Mountain', 2, NULL), ('Mountain', 2, NULL);

#hub3
INSERT INTO equipment (item, hub, returned) VALUES ('highvisjacket', 3, TRUE), ('highvisjacket', 3, TRUE), ('highvisjacket', 3, TRUE), ('highvisjacket', 3, TRUE), ('highvisjacket', 3, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('bag', 3, TRUE), ('bag', 3, TRUE), ('bag', 3, TRUE), ('bag', 3, TRUE), ('bag', 3, TRUE), ('bag', 3, TRUE), ('bag', 3, TRUE), ('bag', 3, TRUE), ('bag', 3, TRUE), ('bag', 3, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('pump', 3, TRUE), ('pump', 3, TRUE), ('pump', 3, TRUE), ('pump', 3, TRUE), ('pump', 3, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('helmet', 3, TRUE), ('helmet', 3, TRUE), ('helmet', 3, TRUE), ('helmet', 3, TRUE), ('helmet', 3, TRUE), ('helmet', 3, TRUE), ('helmet', 3, TRUE), ('helmet', 3, TRUE), ('helmet', 3, TRUE), ('helmet', 3, TRUE), ('helmet', 3, TRUE), ('helmet', 3, TRUE), ('helmet', 3, TRUE), ('helmet', 3, TRUE), ('helmet', 3, TRUE), ('helmet', 3, TRUE), ('helmet', 3, TRUE), ('helmet', 3, TRUE);
INSERT INTO bikes (biketype, hub, damage) VALUES ('CityBike', 3, NULL), ('CityBike', 3, NULL), ('CityBike', 3, NULL), ('CityBike', 3, NULL), ('CityBike', 3, NULL), ('CityBike', 3, NULL), ('CityBike', 3, NULL), ('CityBike', 3, NULL), ('CityBike', 3, NULL), ('CityBike', 3, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Handbike', 3, NULL), ('Handbike', 3, NULL), ('Handbike', 3, NULL), ('Handbike', 3, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Tandem', 3, NULL), ('Tandem', 3, NULL), ('Tandem', 3, NULL), ('Tandem', 3, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Mountain', 3, NULL), ('Mountain', 3, NULL), ('Mountain', 3, NULL), ('Mountain', 3, NULL);

#hub4
INSERT INTO equipment (item, hub, returned) VALUES ('highvisjacket', 4, TRUE), ('highvisjacket', 4, TRUE), ('highvisjacket', 4, TRUE), ('highvisjacket', 4, TRUE), ('highvisjacket', 4, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('bag', 4, TRUE), ('bag', 4, TRUE), ('bag', 4, TRUE), ('bag', 4, TRUE), ('bag', 4, TRUE), ('bag', 4, TRUE), ('bag', 4, TRUE), ('bag', 4, TRUE), ('bag', 4, TRUE), ('bag', 4, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('pump', 4, TRUE), ('pump', 4, TRUE), ('pump', 4, TRUE), ('pump', 4, TRUE), ('pump', 4, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('helmet', 4, TRUE), ('helmet', 4, TRUE), ('helmet', 4, TRUE), ('helmet', 4, TRUE), ('helmet', 4, TRUE), ('helmet', 4, TRUE), ('helmet', 4, TRUE), ('helmet', 4, TRUE), ('helmet', 4, TRUE), ('helmet', 4, TRUE), ('helmet', 4, TRUE), ('helmet', 4, TRUE), ('helmet', 4, TRUE), ('helmet', 4, TRUE), ('helmet', 4, TRUE), ('helmet', 4, TRUE), ('helmet', 4, TRUE), ('helmet', 4, TRUE);
INSERT INTO bikes (biketype, hub, damage) VALUES ('CityBike', 4, NULL), ('CityBike', 4, NULL), ('CityBike', 4, NULL), ('CityBike', 4, NULL), ('CityBike', 4, NULL), ('CityBike', 4, NULL), ('CityBike', 4, NULL), ('CityBike', 4, NULL), ('CityBike', 4, NULL), ('CityBike', 4, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Handbike', 4, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Tandem', 4, NULL), ('Tandem', 4, NULL), ('Tandem', 4, NULL), ('Tandem', 4, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Mountain', 4, NULL), ('Mountain', 4, NULL), ('Mountain', 4, NULL);

#hub5
INSERT INTO equipment (item, hub, returned) VALUES ('highvisjacket', 5, TRUE), ('highvisjacket', 5, TRUE), ('highvisjacket', 5, TRUE), ('highvisjacket', 5, TRUE), ('highvisjacket', 5, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('bag', 5, TRUE), ('bag', 5, TRUE), ('bag', 5, TRUE), ('bag', 5, TRUE), ('bag', 5, TRUE), ('bag', 5, TRUE), ('bag', 5, TRUE), ('bag', 5, TRUE), ('bag', 5, TRUE), ('bag', 5, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('pump', 5, TRUE), ('pump', 5, TRUE), ('pump', 5, TRUE), ('pump', 5, TRUE), ('pump', 5, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('helmet', 5, TRUE), ('helmet', 5, TRUE), ('helmet', 5, TRUE), ('helmet', 5, TRUE), ('helmet', 5, TRUE), ('helmet', 5, TRUE), ('helmet', 5, TRUE), ('helmet', 5, TRUE), ('helmet', 5, TRUE), ('helmet', 5, TRUE), ('helmet', 5, TRUE), ('helmet', 5, TRUE), ('helmet', 5, TRUE), ('helmet', 5, TRUE), ('helmet', 5, TRUE), ('helmet', 5, TRUE), ('helmet', 5, TRUE), ('helmet', 5, TRUE);
INSERT INTO bikes (biketype, hub, damage) VALUES ('CityBike', 5, NULL), ('CityBike', 5, NULL), ('CityBike', 5, NULL), ('CityBike', 5, NULL), ('CityBike', 5, NULL), ('CityBike', 5, NULL), ('CityBike', 5, NULL), ('CityBike', 5, NULL), ('CityBike', 5, NULL), ('CityBike', 5, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Handbike', 5, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Tandem', 5, NULL), ('Tandem', 5, NULL), ('Tandem', 5, NULL), ('Tandem', 5, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Mountain', 5, NULL), ('Mountain', 5, NULL), ('Mountain', 5, NULL);

#hub6
INSERT INTO equipment (item, hub, returned) VALUES ('highvisjacket', 6, TRUE), ('highvisjacket', 6, TRUE), ('highvisjacket', 6, TRUE), ('highvisjacket', 6, TRUE), ('highvisjacket', 6, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('bag', 6, TRUE), ('bag', 6, TRUE), ('bag', 6, TRUE), ('bag', 6, TRUE), ('bag', 6, TRUE), ('bag', 6, TRUE), ('bag', 6, TRUE), ('bag', 6, TRUE), ('bag', 6, TRUE), ('bag', 6, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('pump', 6, TRUE), ('pump', 6, TRUE), ('pump', 6, TRUE), ('pump', 6, TRUE), ('pump', 6, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('helmet', 6, TRUE), ('helmet', 6, TRUE), ('helmet', 6, TRUE), ('helmet', 6, TRUE), ('helmet', 6, TRUE), ('helmet', 6, TRUE), ('helmet', 6, TRUE), ('helmet', 6, TRUE), ('helmet', 6, TRUE), ('helmet', 6, TRUE), ('helmet', 6, TRUE), ('helmet', 6, TRUE), ('helmet', 6, TRUE), ('helmet', 6, TRUE), ('helmet', 6, TRUE), ('helmet', 6, TRUE), ('helmet', 6, TRUE), ('helmet', 6, TRUE);
INSERT INTO bikes (biketype, hub, damage) VALUES ('CityBike', 6, NULL), ('CityBike', 6, NULL), ('CityBike', 6, NULL), ('CityBike', 6, NULL), ('CityBike', 6, NULL), ('CityBike', 6, NULL), ('CityBike', 6, NULL), ('CityBike', 6, NULL), ('CityBike', 6, NULL), ('CityBike', 6, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Handbike', 6, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Tandem', 6, NULL), ('Tandem', 6, NULL), ('Tandem', 6, NULL), ('Tandem', 6, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Mountain', 6, NULL), ('Mountain', 6, NULL), ('Mountain', 6, NULL);

#hub7
INSERT INTO equipment (item, hub, returned) VALUES ('highvisjacket', 7, TRUE), ('highvisjacket', 7, TRUE), ('highvisjacket', 7, TRUE), ('highvisjacket', 7, TRUE), ('highvisjacket', 7, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('bag', 7, TRUE), ('bag', 7, TRUE), ('bag', 7, TRUE), ('bag', 7, TRUE), ('bag', 7, TRUE), ('bag', 7, TRUE), ('bag', 7, TRUE), ('bag', 7, TRUE), ('bag', 7, TRUE), ('bag', 7, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('pump', 7, TRUE), ('pump', 7, TRUE), ('pump', 7, TRUE), ('pump', 7, TRUE), ('pump', 7, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('helmet', 7, TRUE), ('helmet', 7, TRUE), ('helmet', 7, TRUE), ('helmet', 7, TRUE), ('helmet', 7, TRUE), ('helmet', 7, TRUE), ('helmet', 7, TRUE), ('helmet', 7, TRUE), ('helmet', 7, TRUE), ('helmet', 7, TRUE), ('helmet', 7, TRUE), ('helmet', 7, TRUE), ('helmet', 7, TRUE), ('helmet', 7, TRUE), ('helmet', 7, TRUE), ('helmet', 7, TRUE), ('helmet', 7, TRUE), ('helmet', 7, TRUE);
INSERT INTO bikes (biketype, hub, damage) VALUES ('CityBike', 7, NULL), ('CityBike', 7, NULL), ('CityBike', 7, NULL), ('CityBike', 7, NULL), ('CityBike', 7, NULL), ('CityBike', 7, NULL), ('CityBike', 7, NULL), ('CityBike', 7, NULL), ('CityBike', 7, NULL), ('CityBike', 7, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Handbike', 7, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Tandem', 7, NULL), ('Tandem', 7, NULL), ('Tandem', 7, NULL), ('Tandem', 7, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Mountain', 7, NULL), ('Mountain', 7, NULL), ('Mountain', 7, NULL);

#hub8
INSERT INTO equipment (item, hub, returned) VALUES ('highvisjacket', 8, TRUE), ('highvisjacket', 8, TRUE), ('highvisjacket', 8, TRUE), ('highvisjacket', 8, TRUE), ('highvisjacket', 8, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('bag', 8, TRUE), ('bag', 8, TRUE), ('bag', 8, TRUE), ('bag', 8, TRUE), ('bag', 8, TRUE), ('bag', 8, TRUE), ('bag', 8, TRUE), ('bag', 8, TRUE), ('bag', 8, TRUE), ('bag', 8, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('pump', 8, TRUE), ('pump', 8, TRUE), ('pump', 8, TRUE), ('pump', 8, TRUE), ('pump', 8, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('helmet', 8, TRUE), ('helmet', 8, TRUE), ('helmet', 8, TRUE), ('helmet', 8, TRUE), ('helmet', 8, TRUE), ('helmet', 8, TRUE), ('helmet', 8, TRUE), ('helmet', 8, TRUE), ('helmet', 8, TRUE), ('helmet', 8, TRUE), ('helmet', 8, TRUE), ('helmet', 8, TRUE), ('helmet', 8, TRUE), ('helmet', 8, TRUE), ('helmet', 8, TRUE), ('helmet', 8, TRUE), ('helmet', 8, TRUE), ('helmet', 8, TRUE);
INSERT INTO bikes (biketype, hub, damage) VALUES ('CityBike', 8, NULL), ('CityBike', 8, NULL), ('CityBike', 8, NULL), ('CityBike', 8, NULL), ('CityBike', 8, NULL), ('CityBike', 8, NULL), ('CityBike', 8, NULL), ('CityBike', 8, NULL), ('CityBike', 8, NULL), ('CityBike', 8, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Handbike', 8, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Tandem', 8, NULL), ('Tandem', 8, NULL), ('Tandem', 8, NULL), ('Tandem', 8, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Mountain', 8, NULL), ('Mountain', 8, NULL), ('Mountain', 8, NULL);

#hub9
INSERT INTO equipment (item, hub, returned) VALUES ('highvisjacket', 9, TRUE), ('highvisjacket', 9, TRUE), ('highvisjacket', 9, TRUE), ('highvisjacket', 9, TRUE), ('highvisjacket', 9, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('bag', 9, TRUE), ('bag', 9, TRUE), ('bag', 9, TRUE), ('bag', 9, TRUE), ('bag', 9, TRUE), ('bag', 9, TRUE), ('bag', 9, TRUE), ('bag', 9, TRUE), ('bag', 9, TRUE), ('bag', 9, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('pump', 9, TRUE), ('pump', 9, TRUE), ('pump', 9, TRUE), ('pump', 9, TRUE), ('pump', 9, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('helmet', 9, TRUE), ('helmet', 9, TRUE), ('helmet', 9, TRUE), ('helmet', 9, TRUE), ('helmet', 9, TRUE), ('helmet', 9, TRUE), ('helmet', 9, TRUE), ('helmet', 9, TRUE), ('helmet', 9, TRUE), ('helmet', 9, TRUE), ('helmet', 9, TRUE), ('helmet', 9, TRUE), ('helmet', 9, TRUE), ('helmet', 9, TRUE), ('helmet', 9, TRUE), ('helmet', 9, TRUE), ('helmet', 9, TRUE), ('helmet', 9, TRUE);
INSERT INTO bikes (biketype, hub, damage) VALUES ('CityBike', 9, NULL), ('CityBike', 9, NULL), ('CityBike', 9, NULL), ('CityBike', 9, NULL), ('CityBike', 9, NULL), ('CityBike', 9, NULL), ('CityBike', 9, NULL), ('CityBike', 9, NULL), ('CityBike', 9, NULL), ('CityBike', 9, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Handbike', 9, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Tandem', 9, NULL), ('Tandem', 9, NULL), ('Tandem', 9, NULL), ('Tandem', 9, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Mountain', 9, NULL), ('Mountain', 9, NULL), ('Mountain', 9, NULL);

#hub10
INSERT INTO equipment (item, hub, returned) VALUES ('highvisjacket', 10, TRUE), ('highvisjacket', 10, TRUE), ('highvisjacket', 10, TRUE), ('highvisjacket', 10, TRUE), ('highvisjacket', 10, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('bag', 10, TRUE), ('bag', 10, TRUE), ('bag', 10, TRUE), ('bag', 10, TRUE), ('bag', 10, TRUE), ('bag', 10, TRUE), ('bag', 10, TRUE), ('bag', 10, TRUE), ('bag', 10, TRUE), ('bag', 10, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('pump', 10, TRUE), ('pump', 10, TRUE), ('pump', 10, TRUE), ('pump', 10, TRUE), ('pump', 10, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('helmet', 10, TRUE), ('helmet', 10, TRUE), ('helmet', 10, TRUE), ('helmet', 10, TRUE), ('helmet', 10, TRUE), ('helmet', 10, TRUE), ('helmet', 10, TRUE), ('helmet', 10, TRUE), ('helmet', 10, TRUE), ('helmet', 10, TRUE), ('helmet', 10, TRUE), ('helmet', 10, TRUE), ('helmet', 10, TRUE), ('helmet', 10, TRUE), ('helmet', 10, TRUE), ('helmet', 10, TRUE), ('helmet', 10, TRUE), ('helmet', 10, TRUE);
INSERT INTO bikes (biketype, hub, damage) VALUES ('CityBike', 10, NULL), ('CityBike', 10, NULL), ('CityBike', 10, NULL), ('CityBike', 10, NULL), ('CityBike', 10, NULL), ('CityBike', 10, NULL), ('CityBike', 10, NULL), ('CityBike', 10, NULL), ('CityBike', 10, NULL), ('CityBike', 10, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Handbike', 10, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Tandem', 10, NULL), ('Tandem', 10, NULL), ('Tandem', 10, NULL), ('Tandem', 10, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Mountain', 10, NULL), ('Mountain', 10, NULL), ('Mountain', 10, NULL);

#hub11
INSERT INTO equipment (item, hub, returned) VALUES ('highvisjacket', 11, TRUE), ('highvisjacket', 11, TRUE), ('highvisjacket', 11, TRUE), ('highvisjacket', 11, TRUE), ('highvisjacket', 11, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('bag', 11, TRUE), ('bag', 11, TRUE), ('bag', 11, TRUE), ('bag', 11, TRUE), ('bag', 11, TRUE), ('bag', 11, TRUE), ('bag', 11, TRUE), ('bag', 11, TRUE), ('bag', 11, TRUE), ('bag', 11, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('pump', 11, TRUE), ('pump', 11, TRUE), ('pump', 11, TRUE), ('pump', 11, TRUE), ('pump', 11, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('helmet', 11, TRUE), ('helmet', 11, TRUE), ('helmet', 11, TRUE), ('helmet', 11, TRUE), ('helmet', 11, TRUE), ('helmet', 11, TRUE), ('helmet', 11, TRUE), ('helmet', 11, TRUE), ('helmet', 11, TRUE), ('helmet', 11, TRUE), ('helmet', 11, TRUE), ('helmet', 11, TRUE), ('helmet', 11, TRUE), ('helmet', 11, TRUE), ('helmet', 11, TRUE), ('helmet', 11, TRUE), ('helmet', 11, TRUE), ('helmet', 11, TRUE);
INSERT INTO bikes (biketype, hub, damage) VALUES ('CityBike', 11, NULL), ('CityBike', 11, NULL), ('CityBike', 11, NULL), ('CityBike', 11, NULL), ('CityBike', 11, NULL), ('CityBike', 11, NULL), ('CityBike', 11, NULL), ('CityBike', 11, NULL), ('CityBike', 11, NULL), ('CityBike', 11, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Handbike', 11, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Tandem', 11, NULL), ('Tandem', 11, NULL), ('Tandem', 11, NULL), ('Tandem', 11, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Mountain', 11, NULL), ('Mountain', 11, NULL), ('Mountain', 11, NULL);

#hub12
INSERT INTO equipment (item, hub, returned) VALUES ('highvisjacket', 12, TRUE), ('highvisjacket', 12, TRUE), ('highvisjacket', 12, TRUE), ('highvisjacket', 12, TRUE), ('highvisjacket', 12, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('bag', 12, TRUE), ('bag', 12, TRUE), ('bag', 12, TRUE), ('bag', 12, TRUE), ('bag', 12, TRUE), ('bag', 12, TRUE), ('bag', 12, TRUE), ('bag', 12, TRUE), ('bag', 12, TRUE), ('bag', 12, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('pump', 12, TRUE), ('pump', 12, TRUE), ('pump', 12, TRUE), ('pump', 12, TRUE), ('pump', 12, TRUE);
INSERT INTO equipment (item, hub, returned) VALUES ('helmet', 12, TRUE), ('helmet', 12, TRUE), ('helmet', 12, TRUE), ('helmet', 12, TRUE), ('helmet', 12, TRUE), ('helmet', 12, TRUE), ('helmet', 12, TRUE), ('helmet', 12, TRUE), ('helmet', 12, TRUE), ('helmet', 12, TRUE), ('helmet', 12, TRUE), ('helmet', 12, TRUE), ('helmet', 12, TRUE), ('helmet', 12, TRUE), ('helmet', 12, TRUE), ('helmet', 12, TRUE), ('helmet', 12, TRUE), ('helmet', 12, TRUE);
INSERT INTO bikes (biketype, hub, damage) VALUES ('CityBike', 12, NULL), ('CityBike', 12, NULL), ('CityBike', 12, NULL), ('CityBike', 12, NULL), ('CityBike', 12, NULL), ('CityBike', 12, NULL), ('CityBike', 12, NULL), ('CityBike', 12, NULL), ('CityBike', 12, NULL), ('CityBike', 12, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Handbike', 12, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Tandem', 12, NULL), ('Tandem', 12, NULL), ('Tandem', 12, NULL), ('Tandem', 12, NULL);
INSERT INTO bikes (biketype, hub, damage) VALUES ('Mountain', 12, NULL), ('Mountain', 12, NULL), ('Mountain', 12, NULL);

#users
#female
INSERT INTO `users` (`email`,`name`,`addressone`,`city`,`zip`,`dob`,`gender`,`mobile`) VALUES ("quis@tincidunt.edu","September Pickett","P.O. Box 403, 2659 Turpis. Rd.","Carovilli","44188","1973-10-10 ","f","0800 1111"),("penatibus@duiquisaccumsan.com","Cecilia Mcfarland","Ap #742-9213 Vulputate, Street","Nuremberg","8154","1981-05-26 ","f","07863 025165"),("sed.sem.egestas@Integerin.co.uk","Mollie Pate","6273 Mauris, Avenue","Relegem","43642","1967-09-21 ","f","0800 1111"),("rutrum.justo.Praesent@dictumeleifendnunc.edu","Macey Ross","1248 Malesuada Rd.","Kalyan","3410","1990-05-23 ","f","0500 975625"),("venenatis.vel@nonegestasa.org","Chava Dillon","180-4496 Augue, Rd.","Sainte-Marie-sur-Semois","74356-039","1971-02-08 ","f","055 3993 2566"),("habitant@massarutrum.net","Megan Casey","Ap #838-9531 Ornare, Rd.","Bergisch Gladbach","09-535","1968-01-29 ","f","(0111) 768 3496"),("Sed.dictum.Proin@arcuSedeu.ca","Xyla Waller","119-1127 Amet, Rd.","Prince George","08-288","1995-04-27 ","f","0800 999 1436"),("Phasellus.ornare.Fusce@interdumSedauctor.com","Marcia Donovan","Ap #468-8564 Dolor Road","Navadwip","421712","1994-11-29 ","f","0800 1111"),("molestie.in.tempus@nonummyacfeugiat.net","Haviva Koch","P.O. Box 680, 4276 Congue, Ave","Crystal Springs","WP56 1MN","1993-12-08 ","f","055 3494 5733"),("congue.In@tincidunt.com","Kai Macias","6486 Nam Ave","Wilhelmshaven","317789","1967-01-16 ","f","(027) 9584 9893"),("dui.nec@orciadipiscingnon.edu","Dakota Giles","7355 Posuere, St.","Arzano","5352 YF","1967-08-10 ","f","0364 351 0072"),("Mauris.vel@interdum.ca","Uta Faulkner","5988 Elit Ave","Renca","71845","2002-02-02 ","f","(0110) 221 4406"),("pede.nonummy@pellentesqueSeddictum.net","Willow Nolan","9178 Aliquet St.","Ashbourne","885026","1968-11-18 ","f","0500 845344"),("erat@a.edu","Sloane Randall","Ap #607-117 Congue. St.","Cambridge","21217","1982-04-16 ","f","0916 646 7968"),("luctus.et@blandit.edu","Kiona Snow","Ap #445-9652 Parturient Rd.","Peterhead","M3 0TT","1967-03-28 ","f","(016977) 1731"),("lectus.pede.et@laoreetlibero.edu","Iris Merritt","P.O. Box 496, 1791 Velit St.","Zerkegem","62-912","1989-12-22 ","f","0800 1111"),("Vivamus@pharetrautpharetra.com","Kendall Cantu","P.O. Box 102, 5022 Ultricies Av.","Mellet","6977","1966-04-11 ","f","055 1450 9224"),("lobortis.quis@aliquamiaculis.ca","Rhonda Mcbride","Ap #429-7875 Hendrerit Rd.","Linkhout","906635","1976-11-20 ","f","056 2487 4540"),("varius@luctuset.co.uk","Laura Little","7445 Rhoncus Avenue","Ganganagar","90864","1975-01-29 ","f","(028) 3301 2699"),("egestas.Duis.ac@ipsumCurabiturconsequat.edu","Irene Moss","P.O. Box 995, 4445 Egestas. Av.","Morro Reatino","6497","1994-02-20 ","f","0500 072448");
#Male
INSERT INTO `users` (`email`,`name`,`addressone`,`city`,`zip`,`dob`,`gender`,`mobile`) VALUES ("eget.varius@consectetuereuismod.net","Reuben Mejia","P.O. Box 723, 1426 Non Avenue","Germersheim","09-615","2001-06-16 ","m","0845 46 48"),("Donec.fringilla.Donec@pellentesqueafacilisis.com","Castor Roach","P.O. Box 571, 7178 Arcu. Street","Montreal","K3H 8G6","1976-11-28 ","m","07624 228692"),("eu@habitantmorbi.co.uk","Kadeem Winters","4164 Facilisis, Ave","Chambord","81430","1984-03-08 ","m","07624 986607"),("Sed.congue@erosturpisnon.edu","Blake Holcomb","Ap #166-5432 Ultrices. St.","Talca","20688","1974-03-18 ","m","(010048) 30536"),("Aliquam.auctor.velit@semper.ca","Cruz Herring","P.O. Box 382, 4095 Nunc St.","Hope","38977","1972-11-29 ","m","0923 683 1730"),("gravida.Aliquam@consectetueradipiscingelit.com","Rooney Mckinney","Ap #606-7183 Donec Rd.","Coimbatore","12804","1974-03-02 ","m","070 1688 3514"),("semper.rutrum@egestas.co.uk","Kyle Mcintosh","682-6008 Morbi Av.","MalŽves-Sainte-Marie-Wastines","916844","1996-11-22 ","m","056 3334 4597"),("augue@Nuncullamcorper.org","Devin Pitts","374-3242 Enim. Rd.","Gießen","06600","1979-06-15 ","m","055 6567 4576"),("lorem.sit@Suspendisse.net","Silas Alvarado","Ap #336-9722 Curabitur Ave","Chiniot","29349","1977-10-12 ","m","07437 463950"),("diam.vel@egestasblanditNam.net","Charles Austin","P.O. Box 337, 3628 Adipiscing Av.","Liévin","39024","1984-04-08 ","m","07310 671809"),("quis@orciPhasellusdapibus.org","Omar Morales","P.O. Box 360, 891 Pellentesque Av.","Pont-de-Loup","8404 SE","1979-01-14 ","m","(01623) 91334"),("amet.consectetuer@auctor.edu","Cooper Richard","979-5660 Molestie Road","Oberhausen","21697","1973-07-27 ","m","(015314) 45095"),("luctus@nonsapien.co.uk","Zachary Daugherty","985-6283 Orci Avenue","Hull","4993","1969-06-24 ","m","(0101) 793 8622"),("luctus@ornare.com","Micah Bush","231-3459 Eu St.","Tiltil","96731","1996-07-18 ","m","(0171) 464 9919"),("Aliquam.nec.enim@Aliquamfringilla.org","Rafael Pena","2803 Ullamcorper St.","Cras-Avernas","00017","1997-03-19 ","m","070 9593 6458"),("metus@morbitristique.net","Oren Carpenter","727-1216 Mus. Av.","Deventer","76470","1983-06-29 ","m","0845 46 48"),("Donec.tincidunt@pretiumnequeMorbi.ca","Ross Hudson","926-8459 Mauris. Ave","Acciano","07360","1992-04-29 ","m","055 1968 6628"),("felis.eget.varius@vehicula.co.uk","Guy Holt","809-5716 Lobortis Road","Akron","10504","1985-12-21 ","m","0800 574 6191"),("convallis.est@sed.com","Ulric Richmond","P.O. Box 160, 585 Tempus Rd.","White Rock","2274 KM","1986-05-25 ","m","07624 838458"),("odio.semper.cursus@penatibusetmagnis.co.uk","Graham Potts","P.O. Box 603, 9415 Proin Ave","Sparwood","69139","2002-07-15 ","m","07624 216625"),("eu.elit@Suspendisse.edu","Harding Finley","Ap #836-6718 Tellus Road","Bragança","9974","2001-09-26 ","m","070 6461 7311"),("eleifend.nunc@Vivamusnibh.co.uk","Brady Sullivan","430-1222 Neque Rd.","Bridgeport","10776","1982-09-04 ","m","07624 717515"),("erat.vel.pede@etrutrumeu.edu","Levi Brennan","4812 Phasellus Ave","Sint-Stevens-Woluwe","92227","1966-09-01 ","m","076 4693 5045"),("nibh@tempusscelerisquelorem.ca","Abraham Knight","P.O. Box 378, 3118 Pede Ave","Blehen","9921","1987-04-16 ","m","0800 1111"),("eu.placerat@rhoncusDonec.org","Wang Blankenship","P.O. Box 726, 2884 Dui, Av.","Boise","QR6Q 0UG","1993-12-28 ","m","055 2829 7325"),("dolor@ut.net","Merrill Benson","Ap #431-9479 Euismod Road","Winnipeg","11416","1969-09-08 ","m","07624 067346"),("turpis.egestas@utsemNulla.net","Hasad Knight","P.O. Box 242, 6064 Arcu Road","Osogbo","56291","1995-08-30 ","m","0311 243 2751"),("eu@facilisis.co.uk","Reece Figueroa","P.O. Box 710, 1697 Nunc Av.","Burlington","172073","1992-05-02 ","m","0343 643 9026"),("magna@risusatfringilla.org","Dennis Rutledge","9856 Arcu. Ave","Boston","84913","1977-03-31 ","m","(028) 0647 6598"),("lectus.sit@Ut.ca","Kadeem Gaines","2414 Elementum, Rd.","Reno","3437","2001-01-04 ","m","(027) 9913 3644"),("Nam@diamPellentesque.org","Raymond Johnson","Ap #124-399 Tincidunt Street","Aurora","14914","1982-08-28 ","m","0890 389 4358"),("sit.amet.ultricies@Quisquepurus.edu","Orlando Clements","P.O. Box 182, 3098 Nisl Avenue","Zedelgem","7407","1981-11-05 ","m","0800 476 3126"),("nec.diam@eratvolutpatNulla.co.uk","Xenos Long","947-6733 Aliquet Ave","Allerona","9550 AK","1986-12-30 ","m","(028) 9925 0304"),("ultrices.iaculis@Donec.net","Driscoll Mcfadden","9386 Quis Street","Rothesay","59771","2003-01-25 ","m","(018376) 57709"),("sociis.natoque@atnisiCum.co.uk","Merrill Robbins","8448 Dapibus St.","Virelles","33-072","1990-09-14 ","m","056 4830 8093"),("ac@condimentum.org","Timothy Phillips","5264 Hymenaeos. Avenue","Gander","64816","1992-08-01 ","m","(016977) 3925"),("enim.diam.vel@Donecnibhenim.org","Eaton Shaw","354 Mauris St.","Kasteelbrakel","5374","1997-04-05 ","m","0800 673 0010"),("metus.vitae@ultricesDuis.co.uk","Neville Ashley","P.O. Box 440, 6948 Pretium Avenue","Moircy","X2B 6W9","1997-11-06 ","m","(0117) 270 3658"),("vulputate@enimnec.org","Keith Velazquez","6055 Consectetuer Ave","Pickering","567991","1967-08-04 ","m","(011222) 07791"),("Fusce@Integervulputaterisus.edu","Harlan Allison","791-6928 Vivamus Avenue","Port Harcourt","30208","1973-01-16 ","m","0500 932961");

# SOME EXTRA EXAMPLE QUERIES
#an example of getting all bike updates from a user
#SELECT * FROM bikeupdates WHERE bookid = (SELECT id FROM users WHERE email = 'test');
#this selects all journies for a user given a email
#SELECT bookid, lat, lng, time FROM bikeupdates WHERE bookid = (SELECT id FROM booking WHERE pid = (SELECT id FROM users WHERE email = 'test'));											    #

# EXAMPLE USAGE
INSERT INTO booking (pid, bid, lid, bookstart, bookend, returned, code) VALUES (1, 1, 1, '2018-09-14 10:00:00', '2020-09-14 12:00:00', FALSE, FALSE, 18732);
INSERT INTO booking (pid, bid, lid, bookstart, bookend, returned, code) VALUES (1, 2, 1, '2018-09-14 13:00:00', '2020-09-14 15:00:00', FALSE, FALSE, 18732);
INSERT INTO booking (pid, bid, lid, bookstart, bookend, returned, code) VALUES (1, 11, 1, '2018-09-14 15:00:00', '2018-09-14 17:00:00', FALSE, FALSE, 18732);

INSERT INTO booking (pid, bid, lid, bookstart, bookend, returned, code) VALUES (2, 1, 1, '2018-09-14 10:00:00', '2018-09-14 12:00:00', FALSE, FALSE, 18732);
INSERT INTO booking (pid, bid, lid, bookstart, bookend, returned, code) VALUES (2, 2, 1, '2018-09-14 13:00:00', '2018-09-14 15:00:00', FALSE, FALSE, 18732);
INSERT INTO booking (pid, bid, lid, bookstart, bookend, returned, code) VALUES (2, 11, 1, '2018-09-14 15:00:00', '2018-09-14 17:00:00', FALSE, FALSE, 18732);

INSERT INTO booking (pid, bid, lid, bookstart, bookend, returned, code) VALUES (3, 10, 1, '2018-09-14 10:00:00', '2018-09-14 12:00:00', FALSE, FALSE, 18732);
INSERT INTO booking (pid, bid, lid, bookstart, bookend, returned, code) VALUES (3, 15, 2, '2018-09-14 13:00:00', '2018-09-14 15:00:00', FALSE, FALSE, 18732);
INSERT INTO booking (pid, bid, lid, bookstart, bookend, returned, code) VALUES (3, 11, 1, '2018-09-14 15:00:00', '2018-09-14 17:00:00', FALSE, FALSE, 18732);

#equipment checked out
UPDATE equipment SET returned = FALSE WHERE id = 3;

INSERT INTO bikeupdates (bid, lat, lng) VALUES (1, 55.913980, -3.161350);
INSERT INTO bikeupdates (bid, lat, lng) VALUES (1, 55.913980, -3.161340);
INSERT INTO bikeupdates (bid, lat, lng) VALUES (1, 55.913980, -3.161330);
INSERT INTO bikeupdates (bid, lat, lng) VALUES (1, 55.913980, -3.161320);
INSERT INTO bikeupdates (bid, lat, lng) VALUES (1, 55.913980, -3.161310);
INSERT INTO bikeupdates (bid, lat, lng) VALUES (1, 55.913980, -3.161300);

UPDATE booking SET returned = TRUE WHERE pid = 1;

UPDATE equipment SET returned = TRUE WHERE id = 3;

INSERT INTO bikeupdates (bid, lat, lng) VALUES (2, 55.913970, -3.161350);
INSERT INTO bikeupdates (bid, lat, lng) VALUES (2, 55.913980, -3.161340);
INSERT INTO bikeupdates (bid, lat, lng) VALUES (2, 55.913980, -3.161330);
INSERT INTO bikeupdates (bid, lat, lng) VALUES (2, 55.913990, -3.161320);
INSERT INTO bikeupdates (bid, lat, lng) VALUES (2, 55.913980, -3.161310);
INSERT INTO bikeupdates (bid, lat, lng) VALUES (2, 55.913970, -3.161300);
INSERT INTO bikeupdates (bid, lat, lng) VALUES (2, 55.913960, -3.161290);
INSERT INTO bikeupdates (bid, lat, lng) VALUES (2, 55.913950, -3.161280);
INSERT INTO bikeupdates (bid, lat, lng) VALUES (2, 55.913940, -3.161270);

UPDATE booking SET returned = TRUE WHERE pid = 2;
