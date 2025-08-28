package com.openclassrooms.PayMyBuddyAPIWeb.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RelationDTO {
    @NotEmpty(message = "Votre email ne doit pas être vide !")
    @Email(message = "Votre email doit être valide")
    private String email;
}
