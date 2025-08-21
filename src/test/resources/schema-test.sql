-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema paymybuddy_test
-- -----------------------------------------------------
--DROP SCHEMA IF EXISTS `paymybuddy_test` ;

-- -----------------------------------------------------
-- Schema paymybuddy_test
-- -----------------------------------------------------
--CREATE SCHEMA IF NOT EXISTS `paymybuddy_test` DEFAULT CHARACTER SET utf8 ;
--USE `paymybuddy_test` ;

-- -----------------------------------------------------
-- Table `app_user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `app_user` ;

CREATE TABLE IF NOT EXISTS `app_user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `balance` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE UNIQUE INDEX `username_UNIQUE` ON `app_user` (`username` ASC) VISIBLE;

CREATE UNIQUE INDEX `email_UNIQUE` ON `app_user` (`email` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `app_transaction`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `app_transaction` ;

CREATE TABLE IF NOT EXISTS `app_transaction` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id_sender` INT NOT NULL,
  `user_id_receiver` INT NOT NULL,
  `description` VARCHAR(255) NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `fees` DECIMAL(10,3) NOT NULL DEFAULT 0.005,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_transaction_sender`
    FOREIGN KEY (`user_id_sender`)
    REFERENCES `app_user` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_transaction_receiver`
    FOREIGN KEY (`user_id_receiver`)
    REFERENCES `app_user` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `fk_transaction_sender_idx` ON `app_transaction` (`user_id_sender` ASC) VISIBLE;

CREATE INDEX `fk_transaction_receiver_idx` ON `app_transaction` (`user_id_receiver` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `user_friendship`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `user_friendship` ;

CREATE TABLE IF NOT EXISTS `user_friendship` (
  `user_id` INT NOT NULL,
  `friend_id` INT NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`, `friend_id`),
  CONSTRAINT `fk_user_friendship_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `app_user` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_user_friendship_friend`
    FOREIGN KEY (`friend_id`)
    REFERENCES `app_user` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `fk_user_friendship_friend_idx` ON `user_friendship` (`friend_id` ASC) VISIBLE;

CREATE INDEX `fk_user_friendship_user_idx` ON `user_friendship` (`user_id` ASC) INVISIBLE;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
