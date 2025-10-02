package com.openclassrooms.PayMyBuddyAPIWeb.config;

import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import com.openclassrooms.PayMyBuddyAPIWeb.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service Spring Security chargé de récupérer les informations d'un utilisateur
 * pour l'authentification.
 * <p>
 * Cette implémentation utilise l'email comme identifiant unique.
 * Si la table AppUser ne contient pas de rôles, on attribue "USER" par défaut.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AppUserRepository appUserRepository;

    public CustomUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    /**
     * Méthode appelée par Spring Security pour charger un utilisateur à partir de son email.
     *
     * @param email l'adresse email de l'utilisateur
     * @return UserDetails utilisé par Spring Security
     * @throws UsernameNotFoundException si aucun utilisateur n'est trouvé
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Récupère l'utilisateur depuis la base
        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email));

        // Transformation en UserDetails Spring Security
        // username -> email
        // password -> mot de passe haché
        // role -> "USER" par défaut
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail()) // ce sera l’identifiant unique
                .password(user.getPassword())  // le mot de passe déjà haché en BDD
                .roles("USER") // Obligatoire pour Spring Security (sinon exception)
                // Même si notre table AppUser n'a pas de rôle, Spring Security exige au moins une authority.
                // Ici, on attribue "USER" par défaut pour que l'utilisateur puisse s'authentifier
                .build();
    }
}
