package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.UserNotFoundException;
import com.openclassrooms.PayMyBuddyAPIWeb.repository.AppUserRepository;
import com.openclassrooms.PayMyBuddyAPIWeb.service.AppUserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
@Transactional
//Après le test, la transaction est rollback → base vide automatiquement → les autres tests ne sont pas pollués
public class ProfilControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepository appUserRepository;

    @BeforeEach
    public void setUp() {
        AppUser user = new AppUser();
        user.setUserName("TestUserProfil");
        user.setEmail("test@example.com");
        user.setPassword("TUP654123!?");
        user.setBalance(BigDecimal.ZERO);
        appUserRepository.save(user);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void showProfilePage_shouldReturnProfilViewWithUserFromDatabase() throws Exception {
        mockMvc.perform(get("/profil"))
                .andExpect(status().isOk()) // Vérifie le statut HTTP 200
                .andExpect(view().name("profil"))// Vérifie que la vue renvoyée est "profil"
                .andExpect(model().attributeExists("profil"))// Vérifie que le modèle contient "profil"
                .andExpect(model().attribute("profil", hasProperty("username", is("TestUserProfil"))))
                .andExpect(model().attribute("profil", hasProperty("email", is("test@example.com"))));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void updateProfil_shouldUpdateUsernameOnly_whenPasswordNotChanged() throws Exception {
        mockMvc.perform(post("/profil")
                        .param("username", "NouveauUsername") // on change juste le username
                        .param("email", "test@example.com")  // email readonly, il reste le même
                        .param("password", "") // PAS de changement de mot de passe
                .with(csrf())) // <-- IMPORTANT
                .andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().attributeExists("successMessage"))
                .andExpect(model().attribute("successMessage", "Profil mis à jour avec succès !"));

        // Vérifier que le 'username' a bien été mis à jour en base
        AppUser updatedUser = appUserRepository.findByEmail("test@example.com")
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable"));
        assertEquals("NouveauUsername", updatedUser.getUserName());
        assertEquals("test@example.com", updatedUser.getEmail()); // email doit rester le même
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void updateProfil_shouldReturnProfil_whenBindingResultHasErrors() throws Exception {
        mockMvc.perform(post("/profil")
                        .param("username", "") // username vide pour déclencher une erreur de validation
                        .param("email", "test@example.com")
                        .param("password", "") // pas de changement de mdp
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().attributeHasFieldErrors("profil", "username")); // Vérifie qu'il y a une erreur sur le champ username
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void updateProfil_shouldRedirectToLogin_whenPasswordChanged() throws Exception {
        mockMvc.perform(post("/profil")
                        .param("username", "TestUserProfil") // même username
                        .param("email", "test@example.com")
                        .param("password", "NouveauMotDePasse123!") // changement de mdp
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?passwordChanged"));

        // Vérification que le mot de passe a bien été changé en base (haché)
        AppUser updatedUser = appUserRepository.findByEmail("test@example.com")
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable"));
        assertNotEquals("TUP654123!?", updatedUser.getPassword()); // le mdp a changé
    }


    @Test
    @WithMockUser(username = "test@example.com")
    public void updateProfil_shouldReturnProfilWithError_whenUsernameAlreadyUsed() throws Exception {
        // Créons un autre utilisateur pour provoquer le conflit
        AppUser anotherUser = new AppUser();
        anotherUser.setUserName("UsernameExistant");
        anotherUser.setEmail("other@example.com");
        anotherUser.setPassword("Password123!");
        anotherUser.setBalance(BigDecimal.ZERO);
        appUserRepository.save(anotherUser);

        mockMvc.perform(post("/profil")
                        .param("username", "UsernameExistant") // username déjà pris
                        .param("email", "test@example.com")
                        .param("password", "") // pas de changement de mot de passe
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Nom d'utilisateur déjà utilisé !"));
    }

}
