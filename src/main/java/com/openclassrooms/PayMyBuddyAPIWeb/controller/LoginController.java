package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.service.LoginService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Contrôleur Spring MVC responsable de la gestion de la page de connexion.
 * <p>
 * Ce contrôleur affiche le formulaire de login, gère l'affichage des messages
 * d'erreur lorsque l'utilisateur échoue à se connecter et des messages de succès
 * lorsque l'utilisateur se déconnecte.
 * </p>
 *
 * Fonctionnalités principales :
 * <ul>
 *     <li>Afficher le formulaire de connexion</li>
 *     <li>Afficher les erreurs de connexion stockées en session</li>
 *     <li>Afficher un message de succès après déconnexion</li>
 * </ul>
 */
@Slf4j
@Controller
public class LoginController {

    /**
     * Affiche le formulaire de connexion.
     * <p>
     * Cette méthode :
     * <ul>
     *     <li>Récupère les erreurs de login depuis la session et les ajoute au modèle</li>
     *     <li>Supprime les erreurs de la session après affichage pour éviter qu'elles restent visibles</li>
     *     <li>Vérifie si l'utilisateur vient de se déconnecter (paramètre 'logout') et ajoute un message de succès au modèle</li>
     * </ul>
     *
     * @param model  modèle Spring utilisé pour transmettre les attributs à la vue
     * @param session session HTTP de l'utilisateur
     * @param logout paramètre optionnel de l'URL indiquant une déconnexion réussie
     * @return le nom de la vue à afficher (login.html)
     */
    @GetMapping("/login")
    public String showLoginForm(Model model, HttpSession session, @RequestParam(value = "logout", required = false) String logout) {

        log.info("********** Obtenir la page de : CONNEXION **********");

        // Vérifie si une erreur de login a été stockée en session (ex : mauvais email/mdp)
        Object error = session.getAttribute("error");

        if (error != null) {
            // Ajoute l'erreur dans le modèle pour l'afficher dans la vue login.html
            model.addAttribute("error", error);
            // Supprime l'erreur de la session après affichage pour éviter qu'elle reste visible si l'utilisateur recharge la page
            session.removeAttribute("error"); // nettoyer après affichage
        }

        // Vérifie si l'utilisateur vient de se déconnecter
        if (logout != null) {
            model.addAttribute("success", "Déconnexion réussie !");
        }

        return "login";
    }

}
