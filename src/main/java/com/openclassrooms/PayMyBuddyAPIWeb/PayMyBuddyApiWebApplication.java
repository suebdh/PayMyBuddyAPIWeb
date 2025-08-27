package com.openclassrooms.PayMyBuddyAPIWeb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class PayMyBuddyApiWebApplication {

    public static void main(String[] args) {

        log.info("Démarrage de l'application PayMyBuddy API Web...");
        SpringApplication.run(PayMyBuddyApiWebApplication.class, args);
       log.info("Application PayMyBuddy API Web démarrée avec succès !");
    }

}
