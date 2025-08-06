package com.openclassrooms.PayMyBuddyAPIWeb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
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
    private LocalDateTime userCreatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="user_friendship",
            joinColumns=@JoinColumn(name="user_id", foreignKey = @ForeignKey(name = "fk_user_friendship_user")),
            inverseJoinColumns =@JoinColumn(name="friend_id", foreignKey = @ForeignKey(name = "fk_user_friendship_friend")))
    private Set<AppUser> friends = new HashSet<>(); //collection unique sans importance d'ordre

    public void addFriend(AppUser friend) {
        friends.add(friend);
        friend.getFriends().add(this);
    }

    public void removeFriend(AppUser friend) {
        friends.remove(friend);
        friend.getFriends().remove(this);
    }

}
