package com.openclassrooms.PayMyBuddyAPIWeb.service;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.AppUserDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.dto.RegisterDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.dto.TransferFormDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.dto.TransferHistoryDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppTransaction;
import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.AuthenticatedUserNotFoundException;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.EmailAlreadyUsedException;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.UserNotFoundException;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.UsernameAlreadyUsedException;
import com.openclassrooms.PayMyBuddyAPIWeb.repository.AppTransactionRepository;
import com.openclassrooms.PayMyBuddyAPIWeb.repository.AppUserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service principal pour la gestion des utilisateurs et des transactions
 * de l'application PayMyBuddy.
 * <p>
 * Ce service fournit des méthodes pour :
 * <ul>
 *     <li>Créer et mettre à jour un utilisateur</li>
 *     <li>Récupérer l'utilisateur actuellement authentifié</li>
 *     <li>Ajouter un ami à un utilisateur</li>
 *     <li>Récupérer la liste des amis</li>
 *     <li>Récupérer l'historique des transactions (avec ou sans pagination)</li>
 *     <li>Effectuer un transfert d'argent entre utilisateurs</li>
 * </ul>
 * <p>
 * Le service gère également la validation des emails et noms d'utilisateur uniques
 * ainsi que la sécurité des opérations (authentification, autorisation, validation de solde).
 */
@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppTransactionRepository appTransactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Vérifie que l'email fourni n'existe pas déjà en base.
     *
     * @param email l'email à valider
     * @throws EmailAlreadyUsedException si l'email est déjà utilisé
     */
    private void validateEmailUnique(String email) {
        if (appUserRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyUsedException("Email déjà utilisé !");
        }
    }

    /**
     * Vérifie que le nom d'utilisateur fourni n'existe pas déjà en base.
     *
     * @param userName le nom d'utilisateur à valider
     * @throws UsernameAlreadyUsedException si le nom d'utilisateur est déjà utilisé
     */
    private void validateUserNameUnique(String userName) {
        if (appUserRepository.findByUserName(userName).isPresent()) {
            throw new UsernameAlreadyUsedException("Nom d'utilisateur déjà utilisé !");
        }
    }

    /**
     * Crée un nouvel utilisateur à partir d'un {RegisterDTO}.
     * Le mot de passe est haché et le solde initialisé à zéro.
     *
     * @param registerDTO DTO contenant les informations de l'utilisateur
     * @throws EmailAlreadyUsedException si l'email existe déjà
     * @throws UsernameAlreadyUsedException si le nom d'utilisateur existe déjà
     */
    public void createUser(RegisterDTO registerDTO) {

        validateEmailUnique(registerDTO.getEmail());
        validateUserNameUnique(registerDTO.getUserName());

        // Sauvegarder le nouvel utilisateur après hachage du mot de passe
        AppUser newUser = new AppUser();
        newUser.setUserName(registerDTO.getUserName());
        newUser.setEmail(registerDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        newUser.setBalance(BigDecimal.ZERO); // Initialisation solde à zéro pour un nouvel utilisateur

        appUserRepository.save(newUser);

    }

    /**
     * Met à jour un utilisateur existant.
     * Vérifie que l'email et le nom d'utilisateur restent uniques si modifiés.
     *
     * @param userId l'identifiant de l'utilisateur à mettre à jour
     * @param appUserDTO DTO contenant les nouvelles valeurs
     * @throws UserNotFoundException si l'utilisateur n'existe pas
     * @throws EmailAlreadyUsedException si le nouvel email est déjà utilisé
     * @throws UsernameAlreadyUsedException si le nouveau nom d'utilisateur est déjà utilisé
     */
    public void updateUser(int userId, AppUserDTO appUserDTO) {
        // 1- Récupérer l'utilisateur existant
        AppUser existingUser = appUserRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable"));

        // 2- Vérifier si l'email souhaité est déjà pris (On passe l'email du DTO, donc ce que l'utilisateur VEUT mettre)
        // On vérifie l'email seulement si l'utilisateur veut le changer
        if (!existingUser.getEmail().equals(appUserDTO.getEmail())) {
            validateEmailUnique(appUserDTO.getEmail());
        }

        // 2-BIS Vérifier le 'username' uniquement si l'utilisateur veut le changer
        if (!existingUser.getUserName().equals(appUserDTO.getUserName())) {
            validateUserNameUnique(appUserDTO.getUserName());
        }

        // 3- Mettre à jour les champs
        existingUser.setUserName(appUserDTO.getUserName());
        existingUser.setEmail(appUserDTO.getEmail());
        existingUser.setPassword(passwordEncoder.encode(appUserDTO.getPassword()));
        existingUser.setBalance(appUserDTO.getBalance());

        // 4- Sauvegarder le DTO
        convertToDTO(appUserRepository.save(existingUser));
    }

    /**
     * Récupère l'utilisateur actuellement authentifié sous forme de DTO.
     *
     * @return AppUserDTO de l'utilisateur connecté
     * @throws AuthenticatedUserNotFoundException si l'utilisateur connecté n'existe pas
     */
    public AppUserDTO getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // récupère l'email de l'utilisateur connecté
        // getName() retourne l'email (config .withUsername(user.getEmail() de CustomUserDetailsService)
        return appUserRepository.findByEmail(email)
                .map(this::convertToDTO)
                .orElseThrow(() -> new AuthenticatedUserNotFoundException(
                        "Utilisateur connecté introuvable avec l'email : " + email
                ));
    }

    /**
     * Récupère l'utilisateur actuellement authentifié sous forme d'entité.
     *
     * @return {AppUser} connecté
     * @throws AuthenticatedUserNotFoundException si l'utilisateur connecté n'existe pas
     */
    public AppUser getAuthenticatedUserEntity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticatedUserNotFoundException(
                        "Utilisateur connecté introuvable avec l'email : " + email
                ));
    }

    /**
     * Ajoute un utilisateur comme ami en utilisant son email.
     *
     * @param friendEmail email de l'ami à ajouter
     * @throws UserNotFoundException si l'utilisateur à ajouter n'existe pas
     * @throws IllegalArgumentException si l'utilisateur tente de s'ajouter lui-même
     * @throws IllegalStateException si la relation existe déjà
     */
    @Transactional
    public void addFriendByEmail(String friendEmail) {
        // Étape 1 : Récupérer l'utilisateur courant connecté
        AppUser currentUser = getAuthenticatedUserEntity();

        // Étape 2 : Vérifier si l'ami existe en BDD
        AppUser friend = appUserRepository.findByEmail(friendEmail)
                .orElseThrow(() -> new UserNotFoundException("Aucun utilisateur-ami trouvé avec cet email !"));

        // Étape 3 : Garde-fou : Empêcher de s’ajouter soi-même
        if (currentUser.getEmail().equals(friend.getEmail())) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous ajouter vous-même en ami !");
        }

        // Étape 4 : Vérifier si la relation existe déjà
        if (currentUser.getFriends().contains(friend)) {
            throw new IllegalStateException("Cet utilisateur est déjà dans la liste de vos amis !");
        }

        // Étape 5 : Ajouter la relation
        currentUser.addFriend(friend);

        // Étape 6 : Sauvegarder l'utilisateur courant pour persister la relation dans la table de jointure
        // Comme la relation est unidirectionnelle, il suffit de sauvegarder currentUser.
        // La table de jointure sera mise à jour automatiquement.
        // Si la relation était bidirectionnelle, il faudrait aussi sauvegarder friend ou utiliser cascade pour propager.
        appUserRepository.save(currentUser);
    }

    /**
     * Convertit une entité {AppUser} en {AppUserDTO}.
     *
     * @param appUser l'utilisateur à convertir
     * @return DTO correspondant
     */
    public AppUserDTO convertToDTO(AppUser appUser) {
        return new AppUserDTO(
                appUser.getUserId(),
                appUser.getUserName(),
                appUser.getEmail(),
                appUser.getPassword(),
                appUser.getBalance(),
                appUser.getUserCreatedAt()
        );
    }

    /**
     * Récupère la liste des amis de l'utilisateur connecté.
     *
     * @return liste des {AppUser} amis
     */
    public List<AppUser> getFriendsForCurrentUser() {
        AppUser currentUser = getAuthenticatedUserEntity();

        // Convertir le Set en List pour l'utiliser dans Thymeleaf
        return currentUser.getFriends().stream().toList();
    }

    // VERSION SANS PAGINATION
    /**
     * Récupère l'historique complet des transactions de l'utilisateur (sans pagination).
     *
     * @return liste de {TransferHistoryDTO} représentant les transactions
     */
    public List<TransferHistoryDTO> getTransactionHistoryForCurrentUser() {
        AppUser currentUser = getAuthenticatedUserEntity();

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
     * Récupère l'historique paginé des transactions de l'utilisateur.
     *
     * @param page numéro de la page (0-indexée)
     * @param size nombre de transactions par page
     * @return liste de {TransferHistoryDTO} correspondant à la page
     */
    public List<TransferHistoryDTO> getTransactionHistoryForCurrentUser(int page, int size) {
        AppUser currentUser = getAuthenticatedUserEntity();

        Pageable pageable = PageRequest.of(page, size);
        Page<AppTransaction> transactionPage =
                appTransactionRepository.findBySenderOrReceiver(currentUser, currentUser, pageable);

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

    // Utile pour calculer le nombre total de pages en fonction du nombre de transactions par page)
    /**
     * Compte le nombre total de transactions de l'utilisateur connecté.
     *
     * @return nombre total de transactions
     */
    public int countTransactionsForCurrentUser() {
        AppUser currentUser = getAuthenticatedUserEntity();
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
        AppUser sender = getAuthenticatedUserEntity();

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
