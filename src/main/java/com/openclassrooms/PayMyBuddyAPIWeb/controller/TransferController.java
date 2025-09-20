package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.TransferFormDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.dto.TransferHistoryDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import com.openclassrooms.PayMyBuddyAPIWeb.service.AppUserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Contrôleur Spring MVC pour la gestion des transferts d'argent entre utilisateurs.
 * <p>
 * Cette classe permet de :
 * <ul>
 *   <li>Afficher la page de transfert d'argent avec un formulaire de saisie.</li>
 *   <li>Récupérer et afficher la liste des amis de l'utilisateur et l'historique des transactions.</li>
 *   <li>Traiter les transferts d'argent en gérant la validation, les erreurs métiers et les exceptions serveur.</li>
 * </ul>
 *
 * Les vues associées sont rendues via Thymeleaf (transfer.html).
 */
@Slf4j
@Controller
public class TransferController {

    private final AppUserService appUserService;

    public TransferController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    /**
     * Affiche la page de transfert d'argent.
     * <p>
     * Cette méthode prépare le modèle Spring MVC avec :
     * <ul>
     *   <li>Un TransferFormDTO vide pour le formulaire.</li>
     *   <li>La liste des amis de l'utilisateur connecté.</li>
     *   <li>L'historique paginé des transactions.</li>
     *   <li>Les informations de pagination (page courante, nombre total de pages).</li>
     * </ul>
     *
     * @param model le modèle Spring MVC permettant de passer des attributs à la vue
     * @param page  le numéro de page pour la pagination des transactions (par défaut 0)
     * @param size  le nombre de transactions par page (par défaut 5)
     * @return le nom de la vue Thymeleaf {transfer.html}
     */
    @GetMapping("/transfer")
    public String showTransferPage(Model model,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "5") int size) { // size = nombre de transactions par page

        log.info("********** Obtenir la page de : TRANSFERT D'ARGENT **********");
        // 1. Ajoute le DTO vide pour le binding du formulaire
        model.addAttribute("transferForm", new TransferFormDTO());

        // 2. Récupère la liste des amis (username)
        List<AppUser> friends = appUserService.getFriendsForCurrentUser();
        model.addAttribute("friends", friends);

        // 3. Récupère l'historique des transactions
        List<TransferHistoryDTO> transactions = appUserService.getTransactionHistoryForCurrentUser(page, size);
        model.addAttribute("transactions", transactions);

        // 4. Récupération paginée des transactions
        int totalTransactions = appUserService.countTransactionsForCurrentUser();
        int totalPages = (int) Math.ceil((double) totalTransactions / size);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);

        return "transfer"; // correspond à transfer.html dans /templates
    }

    /**
     * Traite la soumission du formulaire de transfert d'argent.
     * <p>
     * Cette méthode :
     * <ul>
     *   <li>Valide les données du formulaire (TransferFormDTO).</li>
     *   <li>Appelle le service AppUserService#processTransfer(TransferFormDTO) pour exécuter le transfert.</li>
     *   <li>Gère les erreurs de validation et les exceptions métiers (ex. solde insuffisant, relation invalide).</li>
     *   <li>Met à jour le modèle avec la liste des amis, l'historique des transactions et les informations de pagination.</li>
     *   <li>Utilise {RedirectAttributes} pour afficher un message de succès après redirection.</li>
     * </ul>
     *
     * @param transferForm       le DTO contenant les informations du transfert (destinataire, montant, etc.)
     * @param bindingResult      les résultats de la validation du formulaire
     * @param model              le modèle Spring MVC pour transmettre des messages et données à la vue
     * @param redirectAttributes permet de passer des attributs (messages de succès) après redirection
     * @return le nom de la vue {transfer} ou une redirection vers {/transfer} après succès
     */
    @PostMapping("/transfer")
    public String handleTransfer(
            @Valid @ModelAttribute("transferForm") TransferFormDTO transferForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Fonction utilitaire pour ajouter les attributs de pagination et amis
        Runnable populateModel = () -> {
            List<AppUser> friends = appUserService.getFriendsForCurrentUser();
            List<TransferHistoryDTO> transactions = appUserService.getTransactionHistoryForCurrentUser(0, 5);
            int totalTransactions = appUserService.countTransactionsForCurrentUser();
            int totalPages = (int) Math.ceil((double) totalTransactions / 5);

            model.addAttribute("friends", friends);
            model.addAttribute("transactions", transactions);
            model.addAttribute("currentPage", 0); // page par défaut
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("size", 5);
        };

        // si erreurs de validation côté champ
        if (bindingResult.hasErrors()) {
            populateModel.run();
            return "transfer";
        }

        try {
            appUserService.processTransfer(transferForm);
            redirectAttributes.addFlashAttribute("successMessage", "Transfert d'argent effectué avec succès.");
            return "redirect:/transfer";
        } catch (IllegalArgumentException e) {
            // erreur métier (ex : solde insuffisant, relation invalide...)
            populateModel.run();
            model.addAttribute("errorMessage", e.getMessage());
            return "transfer";
        } catch (Exception e) {
            populateModel.run();
            model.addAttribute("errorMessage", "Erreur serveur. Réessayez plus tard.");// erreur serveur générale
            return "transfer";
        }
    }
}
