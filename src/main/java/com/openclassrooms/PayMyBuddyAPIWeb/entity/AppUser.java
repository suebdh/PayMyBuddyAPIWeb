package com.openclassrooms.PayMyBuddyAPIWeb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "app_user")
@Setter
@Getter
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int userId;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String userName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime userCreatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="user_friendship",
            joinColumns=@JoinColumn(name="user_id", foreignKey = @ForeignKey(name = "fk_user_friendship_user")),
            inverseJoinColumns =@JoinColumn(name="friend_id", foreignKey = @ForeignKey(name = "fk_user_friendship_friend")))
    private Set<AppUser> friends = new HashSet<>(); //collection unique sans importance d'ordre

    //Toutes les transactions telles que l'utilisateur est l'expéditeur (sender dans AppTransaction).
    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    private Set<AppTransaction> sentTransactions = new HashSet<>();

    //Toutes les transactions telles que l'utilisateur est le destinataire (receiver dans AppTransaction).
    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    private Set<AppTransaction> receivedTransactions = new HashSet<>();

    public void addFriend(AppUser friend) {
        // Ajouter l'ami à la liste des amis de l'utilisateur courant
        friends.add(friend);
        //friend.getFriends().add(this); // Pas besoin de cette ligne pour l'instant,
        // car on souhaite garder la relation unidirectionnelle (uniquement pour gérer l'ajout de bénéficiaires dans le contexte des transferts d'argent)
    }

    public AppUser(int userId, String userName, String email, String password, BigDecimal balance, LocalDateTime userCreatedAt, Set<AppUser> friends) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.balance = balance;
        this.userCreatedAt = userCreatedAt;
        this.friends = friends;
    }

    public AppUser() {}
}
