package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.AppUserDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.dto.ProfilDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.EmailAlreadyUsedException;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.UsernameAlreadyUsedException;
import com.openclassrooms.PayMyBuddyAPIWeb.service.AppUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
