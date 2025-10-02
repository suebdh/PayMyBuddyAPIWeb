package com.openclassrooms.PayMyBuddyAPIWeb.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO représentant un utilisateur de PayMyBuddy.
 * Contient les informations de l'utilisateur et les contraintes de validation.
 */
@Getter
@Setter
public class AppUserDTO {

    /** Identifiant unique */
    private int userId;

    /** Nom d'utilisateur (obligatoire, max 50 caractères) */
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(max = 50, message = "Le nom d'utilisateur ne doit pas dépasser 50 caractères")
    private String userName;

    /** Email (obligatoire, valide, max 100 caractères) */
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    private String email;

    /** Mot de passe (obligatoire, 10-100 caractères) */
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 10, max = 100, message = "Le mot de passe doit contenir entre 10 et 100 caractères")
    private String password;

    /** Solde du compte (obligatoire) */
    @NotNull(message = "Le solde est obligatoire")
    private BigDecimal balance;

    /** Date de création du compte */
    private LocalDateTime userCreatedAt;

    /** Constructeur complet */
    public AppUserDTO(int userId, String userName, String email, String password, BigDecimal balance, LocalDateTime userCreatedAt) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.balance = balance;
        this.userCreatedAt = userCreatedAt;
    }

    /** Constructeur vide */
    public AppUserDTO() {
    }
}
