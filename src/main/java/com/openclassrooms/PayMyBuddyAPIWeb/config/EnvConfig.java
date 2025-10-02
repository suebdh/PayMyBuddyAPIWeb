package com.openclassrooms.PayMyBuddyAPIWeb.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * Configuration de l'environnement pour l'application PayMyBuddy.
 * <p>
 * Cette classe charge les variables d'environnement depuis le fichier .env
 * à la racine du projet en utilisant la librairie Dotenv et les place
 * dans les propriétés système DB_USERNAME et DB_PASSWORD.
 * <p>
 * Les valeurs de connexion à la base de données sont ensuite injectées
 * dans les propriétés système pour être accessibles par Spring Boot.
 * <p>
 * Variables attendues dans le fichier .env : DB_USERNAME et DB_PASSWORD.
 *
 * @author Sarar
 */
@Configuration
public class EnvConfig {

    static {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("DB_USERNAME", Objects.requireNonNull(dotenv.get("DB_USERNAME"), "DB_USERNAME manquant dans le fichier .env"));
        System.setProperty("DB_PASSWORD", Objects.requireNonNull(dotenv.get("DB_PASSWORD"), "DB_PASSWORD manquant dans le fichier .env"));
    }
}
