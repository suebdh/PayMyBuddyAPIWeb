package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.AppUserDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.dto.ProfilDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.EmailAlreadyUsedException;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.UsernameAlreadyUsedException;
import com.openclassrooms.PayMyBuddyAPIWeb.service.AppUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Contrôleur Spring MVC pour la gestion du profil utilisateur.
 * <p>
 * Cette classe permet de :
 * <ul>
 *   <li> Charger et d'afficher la page de profil de l'utilisateur connecté.</li>
 *   <li> Traiter la mise à jour des informations du profil (nom d'utilisateur, mot de passe).</li>
 *   <li> Gérer les cas d'erreurs de validation et les exceptions métier (ex : nom d'utilisateur déjà utilisé).</li>
 * </ul>
 *
 * La vue associée est rendue via Thymeleaf (profil.html).
 */
@Slf4j
@Controller
public class ProfilController {

    @Autowired
    private AppUserService appUserService;

    /**
     * Affiche la page du profil utilisateur.
     * <p>
     * Cette méthode récupère l'utilisateur actuellement authentifié via AppUserService#getAuthenticatedUser()
     * et initialise un ProfilDTO avec les données existantes (nom d'utilisateur, email).
     *
     * @param model le modèle Spring MVC permettant de passer des attributs à la vue
     * @return le nom de la vue Thymeleaf {profil.html}
     */
    @GetMapping("/profil")
    public String showProfilPage(Model model) {

        log.info("********** Obtenir la page de : MODIFICATION PROFIL **********");
        // Récupère l’utilisateur connecté via Spring Security
        AppUserDTO userDTO = appUserService.getAuthenticatedUser(); // ne peut jamais être null

        // Préparer le DTO avec les valeurs existantes
        ProfilDTO profilDTO = new ProfilDTO();
        profilDTO.setUsername(userDTO.getUserName());
        profilDTO.setEmail(userDTO.getEmail());

        model.addAttribute("profil", profilDTO);
        return "profil"; // correspond à profil.html dans /templates
    }

    /**
     * Met à jour les informations du profil utilisateur.
     * <p>
     * Cette méthode :
     * <ul>
     *   <li>Valide les données du formulaire ProfilDTO.</li>
     *   <li>Met à jour l'utilisateur via AppUserService#updateUser(Long, AppUserDTO).</li>
     *   <li>Gère les erreurs de validation (via BindingResult).</li>
     *   <li>Déconnecte l'utilisateur si le mot de passe est modifié, pour forcer une reconnexion.</li>
     *   <li>Retourne la vue du profil avec un message de succès ou d'erreur.</li>
     * </ul>
     *
     * @param profilDTO     les données du formulaire de profil (nom d'utilisateur, email, mot de passe)
     * @param bindingResult les résultats de la validation du formulaire
     * @param request       la requête HTTP (utilisée pour la déconnexion en cas de changement de mot de passe)
     * @param response      la réponse HTTP
     * @param model         le modèle Spring MVC pour transmettre des messages à la vue
     * @return le nom de la vue {profil} ou une redirection vers {/login} si le mot de passe a été modifié
     */
    @PostMapping("/profil")
    public String updateProfil(@Valid @ModelAttribute("profil") ProfilDTO profilDTO,
                               BindingResult bindingResult,
                               HttpServletRequest request,
                               HttpServletResponse response,
                               Model model) {

        // Si des erreurs de validation sont présentes, on renvoie le formulaire
        if (bindingResult.hasErrors()) {
            return "profil"; // Thymeleaf affichera les messages d'erreur
        }

        // Récupérer l'utilisateur connecté
        AppUserDTO userDTO = appUserService.getAuthenticatedUser();

        boolean passwordChanged = profilDTO.getPassword() != null && !profilDTO.getPassword().isEmpty();

        // Préparer un DTO pour la mise à jour
        AppUserDTO updatedUser = new AppUserDTO();
        updatedUser.setUserId(userDTO.getUserId());
        updatedUser.setUserName(profilDTO.getUsername());
        updatedUser.setEmail(userDTO.getEmail()); // email reste inchangé
        updatedUser.setPassword(passwordChanged ? profilDTO.getPassword() : userDTO.getPassword());
        updatedUser.setBalance(userDTO.getBalance());

        try {
            appUserService.updateUser(userDTO.getUserId(), updatedUser);

            if (passwordChanged) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null) {
                    new SecurityContextLogoutHandler().logout(request, response, auth);
                }
                return "redirect:/login?passwordChanged";
            }

            model.addAttribute("successMessage", "Profil mis à jour avec succès !");
            return "profil";

        } catch (UsernameAlreadyUsedException | EmailAlreadyUsedException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "profil";
        }
    }

}
