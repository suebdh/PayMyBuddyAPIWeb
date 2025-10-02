package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.RegisterDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.dto.RelationDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.EmailAlreadyUsedException;
import com.openclassrooms.PayMyBuddyAPIWeb.repository.AppUserRepository;
import com.openclassrooms.PayMyBuddyAPIWeb.service.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
@WithMockUser(username = "current@example.com") // simule un utilisateur connecté
public class RelationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepository appUserRepository;

    @BeforeEach
    void setUp() {
        //appUserService.deleteAllUsers(); // purge la base test
        // création des utilisateurs courant et ami
        // Crée l'utilisateur courant uniquement s'il n'existe pas déjà
        try {
            appUserService.createUser(new RegisterDTO("CurrentUser", "current@example.com", "Password123!"));
        } catch (EmailAlreadyUsedException ignored) {}

        // Crée l'ami uniquement s'il n'existe pas déjà
        try {
            appUserService.createUser(new RegisterDTO("FriendUser", "friend@example.com", "Password123!"));
        } catch (EmailAlreadyUsedException ignored) {}

    }

    // =======================
    // Tests GET /relation
    // =======================
    @Test
    @DisplayName("GET /relation sans flash → doit ajouter un nouveau RelationDTO")
    public void showRelationPage_withoutFlash_shouldReturnRegisterViewWithAddingNewDto() throws Exception {
        mockMvc.perform(get("/relation"))
                .andDo(print()) // affiche le résultat complet de la requête dans la console
                .andExpect(status().isOk())
                .andExpect(view().name("relation"))
                .andExpect(model().attributeExists("relationDto"));

    }

    @Test
    @DisplayName("GET /relation avec flash → doit réutiliser le RelationDTO existant")
    public void showRelationPage_withFlash_shouldReuseDto() throws Exception {
        RelationDTO dto = new RelationDTO();
        dto.setEmail("ami@example.com");

        mockMvc.perform(get("/relation").flashAttr("relationDto", dto))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("relation"))
                .andExpect(model().attributeExists("relationDto"))
                .andExpect(model().attribute("relationDto", org.hamcrest.Matchers.hasProperty("email", org.hamcrest.Matchers.is("ami@example.com"))));
    }

    // =======================
    // Tests POST /relation
    // =======================

    @Test
    @DisplayName("POST /relation avec email vide → redirige avec erreur de validation")
    void postRelation_emptyEmail_shouldRedirectWithErrors() throws Exception {
        mockMvc.perform(post("/relation")
                        .with(csrf()) // ajoute le token CSRF, obligatoire pour une request post
                        .param("email", "")) // email vide pour déclencher la validation
                .andDo(print()) // affiche la requête et la réponse dans la console pour debug
                .andExpect(status().is3xxRedirection()) // le POST redirige (PRG)
                .andExpect(redirectedUrl("/relation")) // redirection vers GET /relation
                .andExpect(flash().attributeExists("relationDto")) // le DTO envoyé dans le flash
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.relationDto")); // les erreurs de validation sont présentes.
    }

    @Test
    @DisplayName("POST /relation avec email valide → succès")
    void postRelation_validEmail_shouldRedirectWithSuccess() throws Exception {

        mockMvc.perform(post("/relation")
                        .with(csrf())
                        .param("email", "friend@example.com"))// ami créé en @BeforeEach
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/relation?success"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("POST /relation avec utilisateur introuvable → erreur")
    void postRelation_userNotFound_shouldRedirectWithBindingError() throws Exception {
        mockMvc.perform(post("/relation")
                        .with(csrf())
                        .param("email", "inconnu@example.com")) // email inexistant
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/relation"))
                .andExpect(flash().attributeExists("relationDto"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.relationDto"));
    }

    @Test
    @DisplayName("POST /relation auto-ajout → erreur")
    void postRelation_addSelf_shouldRedirectWithBindingError() throws Exception {
        mockMvc.perform(post("/relation")
                        .with(csrf())
                        .param("email", "current@example.com")) // email de l'utilisateur connecté
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/relation"))
                .andExpect(flash().attributeExists("relationDto"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.relationDto"));
    }


    @Test
    @DisplayName("POST /relation avec doublon → erreur")
    void postRelation_duplicateFriend_shouldRedirectWithBindingError() throws Exception {
        // Crée un ami temporaire uniquement pour ce test
        RegisterDTO tempFriendDTO = new RegisterDTO(
                "TempFriend",
                "tempfriend@example.com",
                "Password123!"
        );
        appUserService.createUser(tempFriendDTO);

        // Ajouter cet ami une première fois → succès
        appUserService.addFriendByEmail("tempfriend@example.com");

        // Recharger l'utilisateur courant depuis la BDD
        AppUser refreshedUser = appUserRepository.findByEmailWithFriends("current@example.com")
                .orElseThrow();

        // vérifier que l'ami a bien été persisté en base après le premier ajout.
        assertTrue(refreshedUser.getFriends().stream()
                .anyMatch(f -> f.getEmail().equals("tempfriend@example.com")));

        // Essayer de l'ajouter une deuxième fois → doit provoquer IllegalStateException
        mockMvc.perform(post("/relation")
                        .with(csrf())
                        .param("email", "tempfriend@example.com"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/relation"))
                .andExpect(flash().attributeExists("relationDto"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.relationDto"));
    }

}