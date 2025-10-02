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
    private AuthenticationService authenticationService;

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
     * @throws AuthenticatedUserNotFoundException si aucun utilisateur connecté n'est trouvé
     */
    public AppUserDTO getAuthenticatedUser() {
        AppUser currentUser = authenticationService.getAuthenticatedUserEntity();
        return convertToDTO(currentUser);
    }

    /**
     * Ajoute un utilisateur comme ami en utilisant son email.
     *
     * @param friendEmail email de l'ami à ajouter
     * @throws UserNotFoundException si l'utilisateur à ajouter n'existe pas
     * @throws IllegalArgumentException si l'utilisateur tente de s'ajouter lui-même
     * @throws IllegalStateException si la relation existe déjà
     */

    public void addFriendByEmail(String friendEmail) {
        // Étape 1 : Récupérer l'utilisateur courant connecté avec ses amis
        AppUser currentUser = appUserRepository.findByEmailWithFriends(authenticationService.getAuthenticatedUserEntity().getEmail())
                .orElseThrow(() -> new AuthenticatedUserNotFoundException("Utilisateur connecté introuvable"));


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
        AppUser currentUser = authenticationService.getAuthenticatedUserEntity();

        // Convertir le Set en List pour l'utiliser dans Thymeleaf
        return currentUser.getFriends().stream().toList();
    }


}
