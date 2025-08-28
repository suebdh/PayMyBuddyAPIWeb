package com.openclassrooms.PayMyBuddyAPIWeb.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "app_user")
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

    public void addFriend(AppUser friend) {
        // Ajouter l'ami à la liste des amis de l'utilisateur courant
        friends.add(friend);
        //friend.getFriends().add(this); // Pas besoin de cette ligne pour l'instant,
        // car on souhaite garder la relation unidirectionnelle (uniquement pour gérer l'ajout de bénéficiaires dans le contexte des transferts d'argent)
    }

    public void removeFriend(AppUser friend) {
        friends.remove(friend);
        friend.getFriends().remove(this);
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDateTime getUserCreatedAt() {
        return userCreatedAt;
    }

    public Set<AppUser> getFriends() {
        return friends;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setUserCreatedAt(LocalDateTime userCreatedAt) {
        this.userCreatedAt = userCreatedAt;
    }

    public void setFriends(Set<AppUser> friends) {
        this.friends = friends;
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
