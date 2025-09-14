package com.openclassrooms.PayMyBuddyAPIWeb.service;

import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import com.openclassrooms.PayMyBuddyAPIWeb.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service gérant la logique métier liée à l'authentification des utilisateurs.
 * <p>
 * Ce service interagit avec le dépôt AppUserRepository pour récupérer les
 * informations des utilisateurs et utilise PasswordEncoder pour vérifier
 * que le mot de passe fourni correspond au mot de passe haché stocké dans la base.
 * </p>
 *
 * Fonctionnalités principales :
 * <ul>
 *     <li>Authentifier un utilisateur en vérifiant son email et son mot de passe</li>
 *     <li>Vérifier les mots de passe hashés avec BCrypt</li>
 * </ul>
 */
@Service
public class LoginService {

    /** Dépôt permettant d’accéder aux données des utilisateurs */
    private final AppUserRepository appUserRepository;

    /** Bean Spring Security pour encoder et vérifier les mots de passe */
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructeur du service avec injection des dépendances.
     *
     * @param appUserRepository repository pour accéder aux données des utilisateurs
     * @param passwordEncoder   bean pour encoder et vérifier les mots de passe
     */
    public LoginService (AppUserRepository appUserRepository, PasswordEncoder passwordEncoder){
        this.appUserRepository=appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authentifie un utilisateur en vérifiant que l'email existe et que le mot de passe
     * fourni correspond au hash stocké dans la base.
     * <p>
     * La comparaison des mots de passe est réalisée via PasswordEncoder# matches (CharSequence, String),
     * ce qui permet de vérifier correctement un mot de passe clair contre un hash BCrypt.
     * </p>
     *
     * @param email    l'adresse email de l'utilisateur
     * @param password le mot de passe fourni par l'utilisateur (en clair)
     * @return {@code true} si l'utilisateur existe et que le mot de passe correspond,
     *         {@code false} sinon
     */
    public boolean authenticate(String email, String password) {
        Optional<AppUser> userOpt = appUserRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            AppUser user = userOpt.get();
            // Vérifie le mot de passe en clair fourni dans le formulaire avec le hash stocké
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }

}
