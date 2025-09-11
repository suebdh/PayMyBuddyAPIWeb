package com.openclassrooms.PayMyBuddyAPIWeb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class TransferHistoryDTO {
    private String relation;      // nom de l'ami (receiver)
    private String description;   // description du transfert
    private BigDecimal montant;   // montant du transfert
}
