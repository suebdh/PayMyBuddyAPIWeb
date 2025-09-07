package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.AppUserDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.dto.ProfilDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfilController {

    @Autowired
    private AppUserService appUserService;

    @GetMapping("/profil")
    public String showProfilPage(Model model) {
        // Récupère l’utilisateur connecté via Spring Security
        AppUserDTO userDTO = appUserService.getAuthenticatedUser(); // ne peut jamais être null

        // Préparer le DTO avec les valeurs existantes
        ProfilDTO profilDTO = new ProfilDTO();
        profilDTO.setUsername(userDTO.getUserName());
        profilDTO.setEmail(userDTO.getEmail());

        model.addAttribute("profil", profilDTO);
        return "profil"; // correspond à profil.html dans /templates

    }
}
