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

/**
 * Contrôleur Spring MVC pour la gestion des relations utilisateur (ajout d'amis).
 * <p>
 * Cette classe permet de :
 * <ul>
 *   <li> Afficher la page de création d'une nouvelle relation.</li>
 *   <li> Traiter l'ajout d'une relation via l'email de l'utilisateur cible.</li>
 *   <li> Gérer la validation du formulaire et les exceptions métier
 *       (utilisateur non trouvé, erreurs logiques, erreurs inattendues).</li>
 * </ul>
 *
 * La vue associée est rendue via Thymeleaf (relation.html).
 * Utilise le pattern PRG (Post-Redirect-Get) pour éviter la soumission 'multiple' du formulaire.
 */
@Slf4j
@Controller
public class RelationController {

    private final AppUserService appUserService;

    public RelationController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    /**
     * Affiche la page d'ajout d'une nouvelle relation.
     * <p>
     * Cette méthode initialise un RelationDTO si le modèle ne contient pas déjà un DTO existant.
     * Le DTO est utilisé par Thymeleaf pour remplir le formulaire.
     *
     * @param model le modèle Spring MVC permettant de passer des attributs à la vue
     * @return le nom de la vue Thymeleaf {relation.html}
     */
    @GetMapping("/relation")
    public String showRelationPage(Model model) {
        log.info("********** Obtenir la page de : AJOUT NOUVELLE RELATION **********");

        // Si aucun DTO n’existe dans le model, on en crée un
        if (!model.containsAttribute("relationDto")) {
            model.addAttribute("relationDto", new RelationDTO());
        }
        return "relation";
    }

    /**
     * Traite la soumission du formulaire d'ajout d'une relation.
     * <p>
     * Cette méthode :
     * <ul>
     *   <li>Valide les données du formulaire (RelationDTO).</li>
     *   <li>Appelle le service {AppUserService#addFriendByEmail(String)} pour ajouter l'ami.</li>
     *   <li>Gère les erreurs de validation et les exceptions métier
     *       (UserNotFoundException;IllegalArgumentException;IllegalStateException).</li>
     *   <li>Utilise RedirectAttributes pour transmettre les messages de succès ou d'erreur après redirection.</li>
     *   <li>Applique le pattern PRG (Post-Redirect-Get) pour éviter les doublons de soumission.</li>
     * </ul>
     *
     * @param relationDto        le DTO contenant l'email de l'utilisateur à ajouter en ami
     * @param bindingResult      les résultats de la validation du formulaire
     * @param redirectAttributes permet de passer des attributs (messages ou DTO) après redirection
     * @return la redirection vers {/relation} avec un paramètre de succès ou les erreurs associées
     */
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
