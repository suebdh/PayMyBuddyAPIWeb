package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.ProfilDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfilController {

    @GetMapping("/profil")
    public String showProfilPage(Model model) {
        model.addAttribute("profil", new ProfilDTO()); // le strict minimum pour afficher profil
        return "profil"; // correspond à profil.html dans /templates

    }
}
