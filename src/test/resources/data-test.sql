-- (Optionnel) reset propre avant réinsertion
SET FOREIGN_KEY_CHECKS=0;
TRUNCATE TABLE app_transaction;
TRUNCATE TABLE user_friendship;
TRUNCATE TABLE app_user;
SET FOREIGN_KEY_CHECKS=1;

START TRANSACTION;
USE paymybuddy_test;
-- -----------------------------------------------------
-- Insertion de 10 utilisateurs
-- -----------------------------------------------------
INSERT INTO app_user (username, email, password, balance) VALUES
('Alice', 'alice@example.com', 'password123', 500.00),
('Bob', 'bob@example.com', 'password123', 300.00),
('Charlie', 'charlie@example.com', 'password123', 450.00),
('David', 'david@example.com', 'password123', 600.00),
('Eva', 'eva@example.com', 'password123', 350.00),
('Frank', 'frank@example.com', 'password123', 400.00),
('Grace', 'grace@example.com', 'password123', 700.00),
('Hugo', 'hugo@example.com', 'password123', 200.00),
('Ivy', 'ivy@example.com', 'password123', 550.00),
('Jack', 'jack@example.com', 'password123', 250.00);
COMMIT;


START TRANSACTION;
USE paymybuddy_test;
-- -----------------------------------------------------
-- 14 transactions
-- -----------------------------------------------------
INSERT INTO app_transaction (user_id_sender, user_id_receiver, description, amount) VALUES
(1, 2, 'Lunch', 20.00),
(2, 3, 'Gift', 50.00),
(3, 4, 'Taxi', 15.50),
(4, 5, 'Coffee', 5.25),
(5, 6, 'Dinner', 45.00),
(6, 7, 'Movie', 12.00),
(7, 8, 'Book', 25.00),
(8, 9, 'Groceries', 30.00),
(9, 10, 'Snack', 8.50),
(10, 1, 'Bus ticket', 3.00),
(2, 5, 'Game', 60.00),
(3, 7, 'Donation', 100.00),
(4, 6, 'Concert', 75.00),
(5, 8, 'Subscription', 12.50);
COMMIT;

START TRANSACTION;
USE paymybuddy_test;
-- -----------------------------------------------------
-- Quelques amitiés
-- -----------------------------------------------------
INSERT INTO user_friendship (user_id, friend_id) VALUES
(1, 2),
(1, 3),
(2, 4),
(2, 5),
(3, 6),
(4, 7),
(5, 8),
(6, 9),
(7, 10),
(8, 1);
COMMIT;