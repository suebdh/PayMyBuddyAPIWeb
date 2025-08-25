package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.LoginDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.service.LoginService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService){
        this.loginService=loginService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model, HttpSession session) {

        log.info("********** Obtenir la page de : CONNEXION **********");

        // Vérifie si une erreur de login a été stockée en session (ex: mauvais email/mdp)
        Object error = session.getAttribute("error");

        if (error != null) {
            // Ajoute l'erreur dans le modèle pour l'afficher dans la vue login.html
            model.addAttribute("error", error);
            // Supprime l'erreur de la session après affichage pour éviter qu'elle reste visible si l'utilisateur recharge la page
            session.removeAttribute("error"); // nettoyer après affichage
        }
        return "login";
    }

}
