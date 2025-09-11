package com.openclassrooms.PayMyBuddyAPIWeb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "app_transaction")
public class AppTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int transactionId;

    private String description;

    @Column(name = "amount", nullable = false)
    private BigDecimal amountTransaction;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime transactionCreatedAt;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id_sender", nullable = false, foreignKey = @ForeignKey(name = "fk_transaction_sender"))
    private AppUser sender;

    @ManyToOne (fetch =FetchType.EAGER)
    @JoinColumn(name = "user_id_receiver", nullable = false,foreignKey = @ForeignKey(name = "fk_transaction_receiver")
     )
    private AppUser receiver;
}
