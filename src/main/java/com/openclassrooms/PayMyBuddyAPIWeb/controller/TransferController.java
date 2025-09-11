package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.TransferFormDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.dto.TransferHistoryDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import com.openclassrooms.PayMyBuddyAPIWeb.service.AppUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}
