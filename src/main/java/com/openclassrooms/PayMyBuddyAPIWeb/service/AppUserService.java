package com.openclassrooms.PayMyBuddyAPIWeb.service;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.AppUserDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.EmailAlreadyUsedException;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.UserNotFoundException;
import com.openclassrooms.PayMyBuddyAPIWeb.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public List<AppUserDTO> getAllUsers(){
        return ((List<AppUser>) appUserRepository.findAll())
                .stream()  // Transforme la liste en Stream
                .map(this::convertToDTO) // Convertit chaque AppUser en AppUserDTO
                .collect(Collectors.toList()); // Re-transforme en List
    }

    public Optional<AppUserDTO> getUserById(int id){
        return appUserRepository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<AppUserDTO> getUserByUserName(String name){
        return appUserRepository.findByUserName(name)
                .map(this::convertToDTO);
    }

    public Optional<AppUserDTO> getUserByEmail(String email){
        return appUserRepository.findByEmail(email)
                .map(this::convertToDTO);
    }

    public AppUserDTO createUser(AppUserDTO appUserDTO){
        validateEmailUnique(appUserDTO.getEmail());
        // Sauvegarder le nouvel utilisateur après hashage du mpt de passe
        appUserDTO.setPassword(passwordEncoder.encode(appUserDTO.getPassword()));
        return convertToDTO(appUserRepository.save(convertToEntity(appUserDTO)));
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

    public AppUserDTO convertToDTO(AppUser appUser){
        return new AppUserDTO(
                appUser.getUserId(),
                appUser.getUserName(),
                appUser.getEmail(),
                appUser.getPassword(),
                appUser.getBalance(),
                appUser.getUserCreatedAt()
        );

    }

    public AppUser convertToEntity(AppUserDTO appUserDTO){
        return new AppUser(
                appUserDTO.getUserId(),
                appUserDTO.getUserName(),
                appUserDTO.getEmail(),
                appUserDTO.getPassword(),
                appUserDTO.getBalance(),
                appUserDTO.getUserCreatedAt(),
                null
        );

    }
}
