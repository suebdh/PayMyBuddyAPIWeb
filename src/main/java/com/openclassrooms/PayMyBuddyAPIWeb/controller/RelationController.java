package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.RelationDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.UserNotFoundException;
import com.openclassrooms.PayMyBuddyAPIWeb.service.AppUserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
public class RelationController {

    private final AppUserService appUserService;

    public RelationController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/relation")
    public String showRelationPage(Model model) {
        log.info("********** Obtenir la page de : AJOUT NOUVELLE RELATION **********");

        // Si aucun DTO n’existe dans le model, on en crée un
        if (!model.containsAttribute("relationDto")) {
            model.addAttribute("relationDto", new RelationDTO());
        }
        return "relation";
    }

    @PostMapping("/relation")
    public String addRelation(
            @Valid @ModelAttribute("relationDto") RelationDTO relationDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        log.info("Tentative d’ajout d’une relation avec email: {}", relationDto.getEmail());

        // 1. Validation côté formulaire (email vide ou invalide)
        if (bindingResult.hasErrors()) {
            log.warn("Erreur de validation du formulaire - email vide ou invalide !");
            // On garde le DTO et les erreurs pour re-afficher le formulaire
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.relationDto", bindingResult);
            redirectAttributes.addFlashAttribute("relationDto", relationDto);
            return "redirect:/relation";
        }

        try {
            // 2. Appel au service pour ajouter l'ami
            appUserService.addFriendByEmail(relationDto.getEmail());
            log.info("Relation ajoutée avec succès !");
            redirectAttributes.addFlashAttribute("successMessage", "Relation ajoutée avec succès !");
        } catch (UserNotFoundException e) {
            log.warn("Utilisateur non trouvé : {}", e.getMessage());
            bindingResult.rejectValue("email", "error.relationDto", e.getMessage());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.relationDto", bindingResult);
            redirectAttributes.addFlashAttribute("relationDto", relationDto);
            return "redirect:/relation";
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Erreur logique : {}", e.getMessage());
            bindingResult.rejectValue("email", "error.relationDto", e.getMessage());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.relationDto", bindingResult);
            redirectAttributes.addFlashAttribute("relationDto", relationDto);
            return "redirect:/relation";
        } catch (Exception e) {
            log.error("Erreur inattendue : {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Une erreur est survenue lors de l’ajout de la relation !");
        }

        // Redirection GET après traitement du POST (pattern PRG : Post-Redirect-Get)
        // Évite le doublon si l'utilisateur rafraîchit la page
        // et permet de garder l'URL propre tout en affichant un message de succès
        return "redirect:/relation?success";
    }
}
