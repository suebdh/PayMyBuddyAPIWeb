package com.openclassrooms.PayMyBuddyAPIWeb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO représentant l'historique d'un transfert d'argent.
 * <p>
 * Contient le bénéficiaire, la description et le montant du transfert.
 */
@Getter
@Setter
@AllArgsConstructor
public class TransferHistoryDTO {

    /** Nom de l'ami bénéficiaire (receiver)*/
    private String relation;

    /** Description du transfert */
    private String description;

    /** Montant du transfert */
    private BigDecimal montant;   // montant du transfert
}
