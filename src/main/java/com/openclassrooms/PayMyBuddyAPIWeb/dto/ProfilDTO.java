package com.openclassrooms.PayMyBuddyAPIWeb.dto;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO pour la mise à jour du profil utilisateur.
 * <p>
 * Contient le nom d'utilisateur, l'email et un mot de passe optionnel.
 * La validation des champs est appliquée via les annotations Jakarta Validation.
 */
@Getter
@Setter
public class ProfilDTO {

    /** Nom d'utilisateur (obligatoire, max 50 caractères) */
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(max = 50, message = "Le nom d'utilisateur ne doit pas dépasser 50 caractères")
    private String username;

    @NotEmpty(message = "Votre email ne doit pas être vide !")
    @Email(message = "Votre email doit être valide")
    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    private String email;

    /**
     * Mot de passe optionnel :
     * --- l'utilisateur peut laisser vide ("") pour ne pas changer son mot de passe
     * --- s'il renseigne quelque chose, la valeur doit respecter la complexité (>=10, 1 maj, 1 min, 1 chiffre, 1 special)
     *
     * La regex autorise soit la chaîne vide ^$ soit la chaîne conforme à la règle.
     */
    @Pattern(
            regexp = "^$|(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{10,}$",
            message = "Le mot de passe doit être vide (pas de changement) ou contenir au moins 10 caractères, 1 majuscule, 1 minuscule, 1 chiffre et 1 caractère spécial"
    )
    private String password;

}
