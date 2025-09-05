package com.openclassrooms.PayMyBuddyAPIWeb.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class AppUserDTO {

    private int userId;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(max = 50, message = "Le nom d'utilisateur ne doit pas dépasser 50 caractères")
    private String userName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 10, max = 100, message = "Le mot de passe doit contenir entre 10 et 100 caractères")
    private String password;

    @NotNull(message = "Le solde est obligatoire")
    private BigDecimal balance;

    private LocalDateTime userCreatedAt;

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDateTime getUserCreatedAt() {
        return userCreatedAt;
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

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setUserCreatedAt(LocalDateTime userCreatedAt) {
        this.userCreatedAt = userCreatedAt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Constructeur complet
    public AppUserDTO(int userId, String userName, String email, String password, BigDecimal balance, LocalDateTime userCreatedAt) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.balance = balance;
        this.userCreatedAt = userCreatedAt;
    }

    // Constructeur vide (important pour Jackson)
    public AppUserDTO() {
    }
}
