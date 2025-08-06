package com.openclassrooms.PayMyBuddyAPIWeb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

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

    @Column(name = "user_id_sender", nullable = false)
    private int sender;

    @Column(name = "user_id_receiver", nullable = false)
    private int receiver;

    private String description;

    @Column(name = "amount", nullable = false)
    private BigDecimal amountTransaction;

    @Column(nullable = false, precision = 10, scale = 3)
    @ColumnDefault("0.005")
    private BigDecimal fees;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime transactionCreatedAt;

}
