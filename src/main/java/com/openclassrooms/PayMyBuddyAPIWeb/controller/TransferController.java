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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class TransferController {

    private final AppUserService appUserService;

    public TransferController(AppUserService appUserService){
        this.appUserService =appUserService;
    }

    @GetMapping("/transfer")
    public String showTransferPage(Model model) {

        // 1. Ajoute le DTO vide pour le binding du formulaire
        model.addAttribute("transferForm", new TransferFormDTO());

        // 2. Récupère la liste des amis (username)
        List<AppUser> friends = appUserService.getFriendsForCurrentUser();
        model.addAttribute("friends", friends);

        // 3. Récupère l'historique des transactions
        List<TransferHistoryDTO> transactions = appUserService.getTransactionHistoryForCurrentUser();
        model.addAttribute("transactions", transactions);

        return "transfer"; // correspond à transfer.html dans /templates
    }

    @PostMapping("/transfer")
    public String handleTransfer(
            @Valid @ModelAttribute("transferForm") TransferFormDTO transferForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // si erreurs de validation côté champ
        if (bindingResult.hasErrors()) {
            model.addAttribute("friends", appUserService.getFriendsForCurrentUser());
            model.addAttribute("transactions", appUserService.getTransactionHistoryForCurrentUser());
            return "transfer";
        }

        try {
            appUserService.processTransfer(transferForm); // TODO méthode à implémenter dans mon AppUserService
            redirectAttributes.addFlashAttribute("successMessage", "Transfert d'argent effectué avec succès.");
            return "redirect:/transfer";
        } catch (IllegalArgumentException e) {
            // erreur métier (ex : solde insuffisant, relation invalide...)
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("friends", appUserService.getFriendsForCurrentUser());
            model.addAttribute("transactions", appUserService.getTransactionHistoryForCurrentUser());
            return "transfer";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur serveur. Réessayez plus tard.");// erreur serveur générale
            model.addAttribute("friends", appUserService.getFriendsForCurrentUser());
            model.addAttribute("transactions", appUserService.getTransactionHistoryForCurrentUser());
            return "transfer";
        }
    }
}
