package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.RegisterDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class RegisterController {

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        log.info("********** Obtenir la page de : INSCRIPTION **********");
        model.addAttribute("registerDTO", new RegisterDTO());
        return "register"; // correspond à register.html dans src/main/resources/templates
    }


}
