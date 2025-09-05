package com.openclassrooms.PayMyBuddyAPIWeb.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    @NotEmpty(message = "Votre email ne doit pas être vide !")
    @Email(message = "Votre email doit être valide")
    private String email;
    @NotEmpty(message = "Votre mot de passe ne doit pas être vide !")
    @Size(min = 10, message = "Votre mdp doit contenir au minimum 10 caractères")
    private String password;
}
