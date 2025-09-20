package com.openclassrooms.PayMyBuddyAPIWeb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité représentant une transaction d'argent entre utilisateurs.
 * <p>
 * Contient la description, le montant, la date de création, ainsi que l'expéditeur et le destinataire.
 */
@Getter
@Setter
@Entity
@Table(name = "app_transaction")
public class AppTransaction {

    /** Identifiant unique de la transaction */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int transactionId;

    /** Description du transfert */
    private String description;

    /** Montant de la transaction (obligatoire) */
    @Column(name = "amount", nullable = false)
    private BigDecimal amountTransaction;

    /** Date et heure de création de la transaction (générée automatiquement) */
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime transactionCreatedAt;

    /** Utilisateur qui envoie l'argent */
    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id_sender", nullable = false, foreignKey = @ForeignKey(name = "fk_transaction_sender"))
    private AppUser sender;

    /** Utilisateur qui reçoit l'argent */
    @ManyToOne (fetch =FetchType.EAGER)
    @JoinColumn(name = "user_id_receiver", nullable = false,foreignKey = @ForeignKey(name = "fk_transaction_receiver")
     )
    private AppUser receiver;
}
