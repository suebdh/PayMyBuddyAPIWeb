package com.openclassrooms.PayMyBuddyAPIWeb.config;

import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import com.openclassrooms.PayMyBuddyAPIWeb.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AppUserRepository appUserRepository;

    public CustomUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    // Même si notre table AppUser n'a pas de rôle, Spring Security exige au moins une authority.
    // Ici, on attribue "USER" par défaut pour que l'utilisateur puisse s'authentifier (.roles("USER"))
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email));

        // Ici, mon AppUser n’a pas de champ role → on lui met "USER" par défaut
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail()) // ce sera l’identifiant unique
                .password(user.getPassword())  // le mot de passe déjà haché en BDD
                .roles("USER") // obligatoire pour Spring Security (sinon exception)
                .build();
    }
}
