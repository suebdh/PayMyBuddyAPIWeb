-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema paymybuddy
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `paymybuddy` ;

-- -----------------------------------------------------
-- Schema paymybuddy
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `paymybuddy` DEFAULT CHARACTER SET utf8 ;
USE `paymybuddy` ;

-- -----------------------------------------------------
-- Table `app_user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `app_user` ;

CREATE TABLE IF NOT EXISTS `app_user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `balance` DECIMAL(10,2) NULL DEFAULT 0.00,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE)
ENGINE = InnoDB;


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
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `fk_transaction_sender_idx` (`user_id_sender` ASC) VISIBLE,
  INDEX `fk_transaction_receiver_idx` (`user_id_receiver` ASC) VISIBLE,
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


-- -----------------------------------------------------
-- Table `user_friendship`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `user_friendship` ;

CREATE TABLE IF NOT EXISTS `user_friendship` (
  `user_id` INT NOT NULL,
  `friend_id` INT NOT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`, `friend_id`),
  INDEX `fk_user_friendship_friend_idx` (`friend_id` ASC) VISIBLE,
  INDEX `fk_user_friendship_user_idx` (`user_id` ASC) INVISIBLE,
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


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
