package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.LoginDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.service.LoginService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService){
        this.loginService=loginService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute("loginDTO") LoginDTO loginDTO, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "login"; // affiche les erreurs de validation dans le template
        }

        boolean isAuthenticated = loginService.authenticate(loginDTO.getEmail(), loginDTO.getPassword());

        if (isAuthenticated) {
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Login et/ou mot de passe erroné(s)");
            model.addAttribute("loginDTO", loginDTO); // pour afficher à nouveau l'email saisi
            return "login";
        }
    }
}
