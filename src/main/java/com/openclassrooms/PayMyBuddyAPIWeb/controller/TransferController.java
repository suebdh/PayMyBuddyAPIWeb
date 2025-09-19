package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.TransferFormDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.dto.TransferHistoryDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import com.openclassrooms.PayMyBuddyAPIWeb.service.AppUserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class TransferController {

    private final AppUserService appUserService;

    public TransferController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/transfer")
    public String showTransferPage(Model model,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "5") int size) { // size = nombre de transactions par page

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
