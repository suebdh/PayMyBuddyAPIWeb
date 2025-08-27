package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfilController {

    @GetMapping("/profil")
    public String showProfilPage() {
        return "profil"; // correspond à profil.html dans /templates

    }
}
