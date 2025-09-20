package com.openclassrooms.PayMyBuddyAPIWeb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité représentant un utilisateur de l'application PayMyBuddy.
 * <p>
 * Contient les informations de l'utilisateur, le solde, la date de création,
 * ainsi que ses amis et ses transactions envoyées/reçues.
 */
@Entity
@Table(name = "app_user")
@Setter
@Getter
public class AppUser {

    /** Identifiant unique de l'utilisateur */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int userId;

    /** Nom d'utilisateur (unique, obligatoire, max 50 caractères) */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String userName;

    /** Email de l'utilisateur (unique, obligatoire, max 100 caractères) */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /** Mot de passe de l'utilisateur (obligatoire, max 100 caractères) */
    @Column(nullable = false, length = 100)
    private String password;

    /** Solde du compte de l'utilisateur */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance;

    /** Date et heure de création du compte (générée automatiquement) */
    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime userCreatedAt;

    /** Ensemble des amis de l'utilisateur */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="user_friendship",
            joinColumns=@JoinColumn(name="user_id", foreignKey = @ForeignKey(name = "fk_user_friendship_user")),
            inverseJoinColumns =@JoinColumn(name="friend_id", foreignKey = @ForeignKey(name = "fk_user_friendship_friend")))
    private Set<AppUser> friends = new HashSet<>(); //collection unique sans importance d'ordre

    /** Transactions envoyées par l'utilisateur */
    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY) // l'utilisateur est le sender dans AppTransaction
    private Set<AppTransaction> sentTransactions = new HashSet<>();

    /** Transactions reçues par l'utilisateur */
    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY) // l'utilisateur est le receiver dans AppTransaction
    private Set<AppTransaction> receivedTransactions = new HashSet<>();

    /**
     * Ajoute un ami à la liste des amis de l'utilisateur.
     *
     * @param friend l'utilisateur à ajouter comme ami
     */
    public void addFriend(AppUser friend) {
        // Ajouter l'ami à la liste des amis de l'utilisateur courant
        friends.add(friend);
        //friend.getFriends().add(this); // Pas besoin de cette ligne pour l'instant,
        // car on souhaite garder la relation unidirectionnelle (uniquement pour gérer l'ajout de bénéficiaires dans le contexte des transferts d'argent)
    }

    /** Constructeur complet */
    public AppUser(int userId, String userName, String email, String password, BigDecimal balance, LocalDateTime userCreatedAt, Set<AppUser> friends) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.balance = balance;
        this.userCreatedAt = userCreatedAt;
        this.friends = friends;
    }

    /** Constructeur vide */
    public AppUser() {}
}
