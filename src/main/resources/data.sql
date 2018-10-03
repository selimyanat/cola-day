-- Initialize the database with predefined data

-- Populate User table with predefined user
INSERT INTO `users` (`name`, `password`, `company`, `role`) VALUES ('user1', 'password1', 'COKE', 'USER');
INSERT INTO `users` (`name`, `password`, `company`, `role`) VALUES ('user2', 'password2', 'PEPSI', 'USER');


-- Populate Room table with 20 rooms 10 for each company --
INSERT INTO `rooms` (`name`, `owner`) VALUES ('COKE_R01', 'COKE');
INSERT INTO `rooms` (`name`, `owner`) VALUES ('COKE_R02', 'COKE');
INSERT INTO `rooms` (`name`, `owner`) VALUES ('COKE_R03', 'COKE');
INSERT INTO `rooms` (`name`, `owner`) VALUES ('COKE_R04,', 'COKE');
INSERT INTO `rooms` (`name`, `owner`) VALUES ('COKE_R05', 'COKE');
INSERT INTO `rooms` (`name`, `owner`) VALUES ('COKE_R06', 'COKE');
INSERT INTO `rooms` (`name`, `owner`) VALUES ('COKE_R07', 'COKE');
INSERT INTO `rooms` (`name`, `owner`) VALUES ('COKE_R08', 'COKE');
INSERT INTO `rooms` (`name`, `owner`) VALUES ('COKE_R09', 'COKE');
INSERT INTO `rooms` (`name`, `owner`) VALUES ('COKE_R10', 'COKE');
INSERT INTO `rooms` (`name`, `owner`) VALUES ('PEPSI_R01', 'PEPSI');
INSERT INTO `rooms` (`name`, `owner`) VALUES ('PEPSI_R02', 'PEPSI');
INSERT INTO `rooms` (`name`, `owner`) VALUES ('PEPSI_R03', 'PEPSI');
INSERT INTO `rooms` (`name`, `owner`) VALUES ('PEPSI_R04', 'PEPSI');
INSERT INTO `rooms` (`name`, `owner`) VALUES ('PEPSI_R05', 'PEPSI');
INSERT INTO `rooms` (`name`, `owner`) VALUES ('PEPSI_R06', 'PEPSI');
INSERT INTO `rooms` (`name`, `owner`) VALUES ('PEPSI_R07', 'PEPSI');
INSERT INTO `rooms` (`name`, `owner`) VALUES ('PEPSI_R08', 'PEPSI');
INSERT INTO `rooms` (`name`, `owner`) VALUES ('PEPSI_R09', 'PEPSI');
INSERT INTO `rooms` (`name`, `owner`) VALUES ('PEPSI_R10', 'PEPSI');

-- Populate Reservation table
INSERT INTO `reservations` (`time_slot`, `room_id`, `organizer_id`) VALUES ('EIGHT_AM_TO_NINE_AM', 1, 1);