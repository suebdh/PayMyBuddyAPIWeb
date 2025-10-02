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
('Alice', 'alice@example.com', '$2a$12$4hXK2Fkte.mPQij3HXVG.OoT78F2YrZjTiV5RpJ2rBVR/ygzZXgTW', 500.00),
('Bob', 'bob@example.com', '$2a$12$/hq.efDQggWK5YAfVBKRQOW.lXaBTvfouq/oMR8M3GeoLSRbEZ7fC', 300.00),
('Charlie', 'charlie@example.com', '$2a$12$/FZXzOGQX9JemoHZj8GgAeBI7hApn19AQakgrvH1cKMUV75..29Mi', 450.00),
('David', 'david@example.com', '$2a$12$TH5tm03yRUO6TDCihLqrDu4coadZUHemkuJCanf/LfQp9FnVYZ/vW', 600.00),
('Eva', 'eva@example.com', '$2a$12$iYYiNpa/XKYSkJ9CGH1wh.kcMhBpvlw/8hBNYW878wT2rhjGKArQS', 350.00),
('Frank', 'frank@example.com', '$2a$12$fuNUZzxNRrOWEhuHPZuT3eIyUfGlgjKQvhiUI5EQwWQdsCWW.Huse', 400.00),
('Grace', 'grace@example.com', '$2a$12$/x2pA1QLlKa/e1P8KtzRWOBCvFNnpl/JVR9oc8Nhup7XB4CZMGJqe', 700.00),
('Hugo', 'hugo@example.com', '$2a$12$nBVKL4dCm3tYjuUxXQjMM.ubtC83PW/oyGaaBEvdnwcEWKNoSP.RS', 200.00),
('Ivy', 'ivy@example.com', '$2a$12$WLOsd3u1EkuatRdD5NRrTu5UWG7vnhKTKTDc3.iR4y6.0v2Ego1Hq', 550.00),
('Jack', 'jack@example.com', '$2a$12$LjXO2tkGY2Alpvfc0mSWGu.lKD.RUr70DNBAvnWo2wj.7/7jaOuAm', 250.00);
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