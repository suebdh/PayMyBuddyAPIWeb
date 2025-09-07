package com.openclassrooms.PayMyBuddyAPIWeb.service;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.AppUserDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.dto.RegisterDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.AuthenticatedUserNotFoundException;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.EmailAlreadyUsedException;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.UserNotFoundException;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.UsernameAlreadyUsedException;
import com.openclassrooms.PayMyBuddyAPIWeb.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Vérifier si l'email existe déjà
    private void validateEmailUnique(String email) {
        if (appUserRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyUsedException("Email déjà utilisé !");
        }
    }

    //Vérifier si username existe déjà
    private void validateUserNameUnique(String userName) {
        if (appUserRepository.findByUserName(userName).isPresent()) {
            throw new UsernameAlreadyUsedException("Nom d'utilisateur déjà utilisé !");
        }
    }

    public List<AppUserDTO> getAllUsers() {
        return appUserRepository.findAll()
                .stream()  // Transforme la liste en Stream
                .map(this::convertToDTO) // Convertit chaque AppUser en AppUserDTO
                .collect(Collectors.toList()); // Re-transforme en List
    }

    public Optional<AppUserDTO> getUserById(int id) {
        return appUserRepository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<AppUserDTO> getUserByUserName(String name) {
        return appUserRepository.findByUserName(name)
                .map(this::convertToDTO);
    }

    public Optional<AppUserDTO> getUserByEmail(String email) {
        return appUserRepository.findByEmail(email)
                .map(this::convertToDTO);
    }

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

    public AppUserDTO updateUser(int userId, AppUserDTO appUserDTO) {
        // 1- Récupérer l'utilisateur existant
        AppUser existingUser = appUserRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable"));

        // 2- Vérifier si l'email souhaité est déjà pris (On passe l'email du DTO, donc ce que l'utilisateur VEUT mettre)
        // On vérifie l'email seulement si l'utilisateur veut le changer
        if (!existingUser.getEmail().equals(appUserDTO.getEmail())) {
            validateEmailUnique(appUserDTO.getEmail());
        }

        // 3- Mettre à jour les champs
        existingUser.setUserName(appUserDTO.getUserName());
        existingUser.setEmail(appUserDTO.getEmail());
        existingUser.setPassword(passwordEncoder.encode(appUserDTO.getPassword()));
        existingUser.setBalance(appUserDTO.getBalance());
        // TODO A verifier plus tard si j'aurais d'autres champs ajoutés et donc à setter également

        // 4- Sauvegarder et retourner le DTO
        return convertToDTO(appUserRepository.save(existingUser));
    }

    public AppUserDTO getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // récupère l'email de l'utilisateur connecté
        return appUserRepository.findByEmail(email)
                .map(this::convertToDTO)
                .orElseThrow(() -> new AuthenticatedUserNotFoundException(
                        "Utilisateur connecté introuvable avec l'email : " + email
                ));
    }

    public void addFriendByEmail(String friendEmail) {
        // Étape 1 : Récupérer l'utilisateur courant connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = auth.getName();
        // getName() retourne l'email (config .withUsername(user.getEmail() de CustomUserDetailsService)

        AppUser currentUser = appUserRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur courant introuvable !"));

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


}
