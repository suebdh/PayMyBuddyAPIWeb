package com.openclassrooms.PayMyBuddyAPIWeb.service;

import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import com.openclassrooms.PayMyBuddyAPIWeb.repository.AppUserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private final AppUserRepository appUserRepository;

    public LoginService (AppUserRepository appUserRepository){
        this.appUserRepository=appUserRepository;
    }

    public boolean authenticate(String email, String password) {
        Optional<AppUser> userOpt = appUserRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            AppUser user = userOpt.get();
            // Ici, on pourra plus tard utiliser BCryptPasswordEncoder pour comparer les mots de passe hashés
            return user.getPassword().equals(password);
        }
        return false;
    }

}
