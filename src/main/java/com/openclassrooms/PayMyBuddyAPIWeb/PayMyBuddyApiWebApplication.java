package com.openclassrooms.PayMyBuddyAPIWeb;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class PayMyBuddyApiWebApplication {

    // Logger indépendant de Lombok
    //private static final Logger log = LoggerFactory.getLogger(PayMyBuddyApiWebApplication.class);

    public static void main(String[] args) {

        log.info("Démarrage de l'application PayMyBuddy API Web...");
        SpringApplication.run(PayMyBuddyApiWebApplication.class, args);
       log.info("Application PayMyBuddy API Web démarrée avec succès !");
    }

}
