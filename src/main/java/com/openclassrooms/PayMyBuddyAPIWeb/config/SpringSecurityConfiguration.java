package com.openclassrooms.PayMyBuddyAPIWeb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Configuration des règles de sécurité HTTP :
        // - Les URLs /register et /login sont publiques
        // - Toutes les autres URLs nécessitent une authentification
        // - Configuration du formulaire de login avec page custom et redirection après succès
        // - Activation du logout (déconnexion)
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login").permitAll() // pages publiques
                        .anyRequest().authenticated() // le reste nécessite login
                )
                .formLogin(form -> form
                        .loginPage("/login") // login.html
                        .defaultSuccessUrl("/home", true) // page après login
                        .permitAll()
                )
                .logout(logout -> logout.permitAll());

        return http.build();
    }

    // Bean pour encoder les mots de passe
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}