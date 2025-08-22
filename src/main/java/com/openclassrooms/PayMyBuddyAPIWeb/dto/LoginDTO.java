package com.openclassrooms.PayMyBuddyAPIWeb.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class LoginDTO {

    @NotEmpty(message = "Votre email ne doit pas être vide !")
    @Email(message = "Votre email doit être valide")
    private String email;
    @NotEmpty(message = "Votre mot de passe ne doit pas être vide !")
    @Size(min = 10, message = "Votre mdp doit contenir au minimum 10 caractères")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Constructeur vide (important pour Jackson)
    public LoginDTO() {
    }
}
