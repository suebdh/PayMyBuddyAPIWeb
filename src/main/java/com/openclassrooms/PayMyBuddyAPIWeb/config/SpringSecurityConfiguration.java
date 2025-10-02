package com.openclassrooms.PayMyBuddyAPIWeb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de la sécurité de l'application PayMyBuddy.
 * <p>
 * Cette classe définit :
 * <ul>
 *   <li>Les règles d'accès aux URLs (pages publiques et protégées).</li>
 *   <li>La configuration du formulaire de login personnalisé avec gestion des échecs.</li>
 *   <li>La configuration du logout.</li>
 *   <li>Le bean PasswordEncoder pour encoder les mots de passe avec BCrypt.</li>
 * </ul>
 * <p>
 * URLs publiques : /register, /login, et les ressources statiques (/css/**, /js/**, /images/**).
 * Toutes les autres URLs nécessitent une authentification.
 *
 * @author Sarar
 */
@Configuration
public class SpringSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Configuration des règles de sécurité HTTP
        // Configuration du formulaire de login avec page custom et redirection après succès
        // Activation du logout (déconnexion)
        http
                .authorizeHttpRequests(auth -> auth
                        // autorise
                        .requestMatchers("/register", "/login").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        // toutes les autres requêtes nécessitent authentification
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // page custom : login.html
                        .defaultSuccessUrl("/transfer", true) // redirection après succès
                        // Gestion personnalisée de l’échec de connexion :
                        // si les identifiants sont incorrects, on stocke un message d'erreur dans la session et on redirige vers /login
                        .failureHandler((request, response, exception) -> {
                            request.getSession().setAttribute("error", "Email et/ou mot de passe incorrect(s).");
                            response.sendRedirect("/login");
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());



        return http.build();
    }

    // Bean pour encoder les mots de passe
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}