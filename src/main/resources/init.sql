-- Initialize the database with predefined data

-- Populate User table with predefined user
INSERT INTO users (id, name, password, company, role) VALUES (1, 'user1', 'password1', 'COKE',
'USER');
INSERT INTO users (id, name, password, company, role) VALUES (2, 'user2', 'password2', 'PEPSI',
'USER')
;


-- Populate Room table with 20 rooms 10 for each company --
INSERT INTO rooms (id, name, owner) VALUES (1, 'COKE_R01', 'COKE');
INSERT INTO rooms (id, name, owner) VALUES (2, 'COKE_R02', 'COKE');
INSERT INTO rooms (id, name, owner) VALUES (3, 'COKE_R03', 'COKE');
INSERT INTO rooms (id, name, owner) VALUES (4, 'COKE_R04,', 'COKE');
INSERT INTO rooms (id, name, owner) VALUES (5, 'COKE_R05', 'COKE');
INSERT INTO rooms (id, name, owner) VALUES (6, 'COKE_R06', 'COKE');
INSERT INTO rooms (id, name, owner) VALUES (7, 'COKE_R07', 'COKE');
INSERT INTO rooms (id, name, owner) VALUES (8, 'COKE_R08', 'COKE');
INSERT INTO rooms (id, name, owner) VALUES (9, 'COKE_R09', 'COKE');
INSERT INTO rooms (id, name, owner) VALUES (10, 'COKE_R10', 'COKE');
INSERT INTO rooms (id, name, owner) VALUES (11, 'PEPSI_R01', 'PEPSI');
INSERT INTO rooms (id, name, owner) VALUES (12, 'PEPSI_R02', 'PEPSI');
INSERT INTO rooms (id, name, owner) VALUES (13, 'PEPSI_R03', 'PEPSI');
INSERT INTO rooms (id, name, owner) VALUES (14, 'PEPSI_R04', 'PEPSI');
INSERT INTO rooms (id, name, owner) VALUES (15, 'PEPSI_R05', 'PEPSI');
INSERT INTO rooms (id, name, owner) VALUES (16, 'PEPSI_R06', 'PEPSI');
INSERT INTO rooms (id, name, owner) VALUES (17, 'PEPSI_R07', 'PEPSI');
INSERT INTO rooms (id, name, owner) VALUES (18, 'PEPSI_R08', 'PEPSI');
INSERT INTO rooms (id, name, owner) VALUES (19, 'PEPSI_R09', 'PEPSI');
INSERT INTO rooms (id, name, owner) VALUES (20, 'PEPSI_R10', 'PEPSI');

-- Populate Reservation table (hack for the id)
INSERT INTO reservations (id, time_slot, room_id, organizer_id) VALUES (10000,
'EIGHT_AM_TO_NINE_AM', 1,
 1);