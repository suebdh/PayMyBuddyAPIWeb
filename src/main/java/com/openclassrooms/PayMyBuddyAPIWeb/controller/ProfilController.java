package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.AppUserDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.dto.ProfilDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.UserNotFoundException;
import com.openclassrooms.PayMyBuddyAPIWeb.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfilController {

    @Autowired
    private AppUserService appUserService;

    @GetMapping("/profil")
    public String showProfilPage(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // l'email ou identifiant de l'utilisateur connecté

        // Récupérer l'utilisateur depuis la base via un service
        AppUserDTO userDTO = appUserService.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'email : " + email));

        // Préparer le DTO avec les valeurs existantes
        ProfilDTO profilDTO = new ProfilDTO();
        profilDTO.setUsername(userDTO.getUserName());
        profilDTO.setEmail(userDTO.getEmail());

        model.addAttribute("profil", profilDTO);
        return "profil"; // correspond à profil.html dans /templates

    }
}
