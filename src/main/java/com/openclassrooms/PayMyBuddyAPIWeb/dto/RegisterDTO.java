package com.openclassrooms.PayMyBuddyAPIWeb.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO pour l'inscription d'un nouvel utilisateur.
 * <p>
 * Contient le nom d'utilisateur, l'email et le mot de passe avec validation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {

    /** Nom d'utilisateur (obligatoire, max 50 caractères) */
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(max = 50, message = "Le nom d'utilisateur ne doit pas dépasser 50 caractères")
    private String userName;

    /** Email (obligatoire et valide) */
    @NotEmpty(message = "Votre email ne doit pas être vide !")
    @Email(message = "Votre email doit être valide")
    private String email;

    /**
     * Mot de passe (obligatoire, minimum 10 caractères) :
     * doit contenir au moins 1 majuscule, 1 minuscule, 1 chiffre et 1 caractère spécial
     */
    @NotEmpty(message = "Votre mot de passe ne doit pas être vide !")
    @Size(min = 10, message = "Votre mdp doit contenir au minimum 10 caractères")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{10,}$",
            message = "Le mot de passe doit contenir au moins 1 majuscule, 1 minuscule, 1 chiffre et 1 caractère spécial"
    )
    private String password;

}
