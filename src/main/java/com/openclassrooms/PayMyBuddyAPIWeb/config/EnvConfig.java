package com.openclassrooms.PayMyBuddyAPIWeb.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

    @Configuration
    public class EnvConfig {

        static {
            Dotenv dotenv = Dotenv.load();
            System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
            System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        }
    }
