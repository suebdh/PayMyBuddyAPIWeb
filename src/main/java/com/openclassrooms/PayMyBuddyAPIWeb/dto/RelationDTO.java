package com.openclassrooms.PayMyBuddyAPIWeb.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO pour ajouter une relation (ami) via email.
 * <p>
 * Contient uniquement l'adresse email de l'utilisateur à ajouter, avec validation.
 */
@Setter
@Getter
@NoArgsConstructor
public class RelationDTO {

    /** Email de l'utilisateur à ajouter (obligatoire et valide) */
    @NotEmpty(message = "Votre email ne doit pas être vide !")
    @Email(message = "Votre email doit être valide")
    private String email;
}
