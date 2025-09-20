package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.RegisterDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.EmailAlreadyUsedException;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.UsernameAlreadyUsedException;
import com.openclassrooms.PayMyBuddyAPIWeb.service.AppUserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Contrôleur Spring MVC responsable de la gestion des inscriptions utilisateurs.
 * <p>
 * Ce contrôleur permet d'afficher le formulaire d'inscription  GET /register
 * et de traiter la soumission de ce formulaire POST /register
 * </p>
 *
 * <ul>
 *   <li>Affiche la page d'inscription avec un {RegisterDTO} vide.</li>
 *   <li>Valide les données saisies par l'utilisateur.</li>
 *   <li>Gère les erreurs de validation standards et les cas particuliers
 *       où l'adresse email ou le nom d'utilisateur sont déjà utilisés.</li>
 *   <li>Redirige vers la page de connexion après une inscription réussie.</li>
 * </ul>
 *
 * @author
 */
@Slf4j
@Controller
public class RegisterController {

    @Autowired
    private AppUserService appUserService;

    // Méthode GET /register : afficher le formulaire d'inscription
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        log.info("********** Obtenir la page de : INSCRIPTION **********");
        model.addAttribute("registerDTO", new RegisterDTO());
        return "register"; // correspond à register.html dans src/main/resources/templates
    }


    // Méthode POST /register : traitement de la soumission du formulaire d'inscription
    @PostMapping("/register")
    public String registerUser(@Valid RegisterDTO registerDTO,
                               BindingResult bindingResult,
                               Model model) {
        log.info("********** POST /register : tentative d'inscription **********");

        if (bindingResult.hasErrors()) {
            log.info("Formulaire invalide, renvoi vers register.html avec affichage des erreurs");
            return "register";
        }

        try {
            appUserService.createUser(registerDTO); // logique pour créer l'utilisateur
        } catch (EmailAlreadyUsedException | UsernameAlreadyUsedException e) {
            log.debug("Exception lors de la création de l'utilisateur : {}", e.getMessage());

            // déterminer le champ en erreur
            String field = e instanceof EmailAlreadyUsedException ? "email" : "userName";
            String fieldValue = e instanceof EmailAlreadyUsedException ? registerDTO.getEmail() : registerDTO.getUserName();

            // ajouter une erreur personnalisée à BindingResult
            bindingResult.addError(new FieldError(
                    "registerDTO",
                    field,
                    fieldValue,
                    false,
                    new String[]{"error.registerDTO"},
                    null,
                    e.getMessage()
            ));

            return "register"; // renvoyer sur le formulaire avec message d'erreur
        }

        log.info("Utilisateur créé avec succès, redirection vers /login");
        return "redirect:/login?registered";
    }
}

