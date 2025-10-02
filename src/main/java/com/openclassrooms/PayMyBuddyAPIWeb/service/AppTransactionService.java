package com.openclassrooms.PayMyBuddyAPIWeb.service;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.TransferFormDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.dto.TransferHistoryDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppTransaction;
import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import com.openclassrooms.PayMyBuddyAPIWeb.repository.AppTransactionRepository;
import com.openclassrooms.PayMyBuddyAPIWeb.repository.AppUserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppTransactionService {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppTransactionRepository appTransactionRepository;

    // VERSION SANS PAGINATION
    /**
     * Récupère l'historique complet des transactions de l'utilisateur (sans pagination).
     *
     * @return liste de {TransferHistoryDTO} représentant les transactions
     */
    public List<TransferHistoryDTO> getTransactionHistoryForCurrentUser() {
        AppUser currentUser = authenticationService.getAuthenticatedUserEntity();

        // Récupérer toutes les transactions envoyées (l'utilisateur est l'expéditeur)
        List<TransferHistoryDTO> sent = currentUser.getSentTransactions().stream()
                .map(tx -> new TransferHistoryDTO(
                        tx.getReceiver().getUserName(), //celui qui a reçu
                        tx.getDescription(),
                        tx.getAmountTransaction().negate() // montant négatif, car l'utilisateur a payé
                ))
                .toList();

        // Récupérer toutes les transactions reçues (l'utilisateur est le destinataire)
        List<TransferHistoryDTO> received = currentUser.getReceivedTransactions().stream()
                .map(tx -> new TransferHistoryDTO(
                        tx.getSender().getUserName(), // celui qui a envoyé
                        tx.getDescription(),
                        tx.getAmountTransaction() // montant positif, car l'utilisateur l'a reçu
                ))
                .toList();

        // Fusionner les deux
        List<TransferHistoryDTO> all = new java.util.ArrayList<>();
        all.addAll(sent);
        all.addAll(received);

        return all;
    }

    // NOUVELLE VERSION PAGINÉE
    /**
     * Récupère l'historique paginé des transactions de l'utilisateur,
     *  trié de la plus récente à la plus ancienne.
     *
     * @param page numéro de la page (0-indexée)
     * @param size nombre de transactions par page
     * @return liste de {TransferHistoryDTO} correspondant à la page
     */
    public List<TransferHistoryDTO> getTransactionHistoryForCurrentUser(int page, int size) {
        AppUser currentUser = authenticationService.getAuthenticatedUserEntity();

        Pageable pageable = PageRequest.of(page, size);
        Page<AppTransaction> transactionPage =
                appTransactionRepository.findBySenderOrReceiverOrderByTransactionCreatedAtDesc(currentUser, currentUser, pageable);

        return transactionPage.stream()
                .map(tx -> {
                    if (tx.getSender().equals(currentUser)) {
                        return new TransferHistoryDTO(
                                tx.getReceiver().getUserName(),
                                tx.getDescription(),
                                tx.getAmountTransaction().negate()
                        );
                    } else {
                        return new TransferHistoryDTO(
                                tx.getSender().getUserName(),
                                tx.getDescription(),
                                tx.getAmountTransaction()
                        );
                    }
                })
                .toList();
    }

    // Utile pour calculer le nombre total de pages en fonction du nombre de transactions par page
    /**
     * Compte le nombre total de transactions de l'utilisateur connecté.
     *
     * @return nombre total de transactions
     */
    public int countTransactionsForCurrentUser() {
        AppUser currentUser = authenticationService.getAuthenticatedUserEntity();
        return appTransactionRepository.countBySenderOrReceiver(currentUser, currentUser);
    }

    /**
     * Traite un transfert d'argent entre l'utilisateur actuellement connecté (expéditeur)
     * et un ami sélectionné (destinataire).
     * <p>
     * La méthode effectue les étapes suivantes :
     * <ol>
     *     <li>Récupère l'utilisateur authentifié (expéditeur) via le contexte de sécurité.</li>
     *     <li>Récupère le destinataire en utilisant le nom d'utilisateur fourni dans {TransferFormDTO}.</li>
     *     <li>Vérifie que l'expéditeur ne tente pas de se transférer de l'argent à lui-même.</li>
     *     <li>Vérifie que le destinataire est bien un ami de l'expéditeur.</li>
     *     <li>Vérifie que le montant du transfert est valide (non nul et positif).</li>
     *     <li>Vérifie que le solde de l'expéditeur est suffisant pour effectuer le transfert.</li>
     *     <li>Met à jour les soldes de l'expéditeur et du destinataire.</li>
     *     <li>Persiste les changements dans la base de données pour les deux utilisateurs.</li>
     *     <li>Crée et enregistre un objet {AppTransaction} représentant le transfert.</li>
     * </ol>
     *
     * @param dto objet {TransferFormDTO} contenant :
     *            <ul>
     *                <li>relation : nom d'utilisateur du destinataire</li>
     *                <li>description : description du transfert</li>
     *                <li>montant : montant du transfert</li>
     *            </ul>
     *
     * @throws IllegalArgumentException si :
     *         <ul>
     *             <li>le destinataire n'existe pas</li>
     *             <li>l'expéditeur tente de se transférer de l'argent à lui-même</li>
     *             <li>le destinataire n'est pas un ami</li>
     *             <li>le montant est nul ou négatif</li>
     *             <li>le solde de l'expéditeur n'est pas suffisant</li>
     *         </ul>
     */
    @Transactional
    public void processTransfer(@Valid TransferFormDTO dto) {
        // Récupère l'utilisateur authentifié sous forme d'entité
        AppUser sender = authenticationService.getAuthenticatedUserEntity();

        // Cherche le destinataire
        AppUser receiver = appUserRepository.findByUserName(dto.getRelation())
                .orElseThrow(() -> new IllegalArgumentException("Relation introuvable"));

        if (sender.getUserId() ==receiver.getUserId()) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous payer vous-même.");
        }

        // Vérifie que le receiver est bien un ami
        boolean areFriends = sender.getFriends().contains(receiver);
        if (!areFriends) {
            throw new IllegalArgumentException("La relation sélectionnée n'est pas dans votre liste d'amis.");
        }

        BigDecimal amount = dto.getMontant();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Montant invalide.");
        }

        // Vérifie le solde disponible
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Solde insuffisant.");
        }

        // Met à jour les soldes
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        // Sauvegarde les entités
        appUserRepository.save(sender);
        appUserRepository.save(receiver);

        // Enregistre la transaction
        AppTransaction tx = new AppTransaction();
        tx.setSender(sender);
        tx.setReceiver(receiver);
        tx.setAmountTransaction(amount);
        tx.setDescription(dto.getDescription());
        tx.setTransactionCreatedAt(LocalDateTime.now());
        appTransactionRepository.save(tx);
    }
}
