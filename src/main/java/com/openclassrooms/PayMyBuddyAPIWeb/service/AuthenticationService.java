package com.openclassrooms.PayMyBuddyAPIWeb.service;

import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.AuthenticatedUserNotFoundException;
import com.openclassrooms.PayMyBuddyAPIWeb.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private AppUserRepository appUserRepository;

    /**
     * Récupère l'utilisateur actuellement authentifié sous forme d'entité.
     *
     * @return {AppUser} connecté
     * @throws AuthenticatedUserNotFoundException si l'utilisateur connecté n'existe pas
     */
    public AppUser getAuthenticatedUserEntity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticatedUserNotFoundException(
                        "Utilisateur connecté introuvable avec l'email : " + email
                ));
    }
}




