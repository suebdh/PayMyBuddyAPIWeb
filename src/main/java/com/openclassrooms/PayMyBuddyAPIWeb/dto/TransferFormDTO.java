package com.openclassrooms.PayMyBuddyAPIWeb.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO pour le formulaire de transfert d'argent.
 * <p>
 * Contient le bénéficiaire, la description et le montant avec validation.
 */
@Getter
@Setter
public class TransferFormDTO {

    /** Nom d'utilisateur du bénéficiaire (obligatoire) */
    @NotBlank(message = "La relation (bénéficiaire) est obligatoire")
    private String relation; // username du friend choisi

    /** Description du transfert (obligatoire, max 255 caractères) */
    @NotBlank(message = "La description est obligatoire")
    @Size(max = 255, message = "La description ne peut pas dépasser 255 caractères")
    private String description;

    /** Montant du transfert (obligatoire, >0, max 2 décimales) */
    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    @Digits(integer = 10, fraction = 2, message = "Le montant doit avoir au maximum 2 décimales")
    private BigDecimal montant;
}
