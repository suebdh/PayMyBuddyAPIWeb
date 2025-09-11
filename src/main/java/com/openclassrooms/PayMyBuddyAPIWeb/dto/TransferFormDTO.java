package com.openclassrooms.PayMyBuddyAPIWeb.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferFormDTO {

    @NotBlank(message = "La relation (bénéficiaire) est obligatoire")
    private String relation; // username du friend choisi

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 255, message = "La description ne peut pas dépasser 255 caractères")
    private String description;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    @Digits(integer = 10, fraction = 2, message = "Le montant doit avoir au maximum 2 décimales")
    private BigDecimal montant;
}
