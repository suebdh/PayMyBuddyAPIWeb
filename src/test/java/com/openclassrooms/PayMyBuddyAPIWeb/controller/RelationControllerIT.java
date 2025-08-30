package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.RelationDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.UserNotFoundException;
import com.openclassrooms.PayMyBuddyAPIWeb.service.AppUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
@WithMockUser // utilisateur connecté par défaut
public class RelationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserService appUserService; // ce sera le mock injecté

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AppUserService appUserService() {
            return Mockito.mock(AppUserService.class); // le mock est injecté
        }
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
        // Pré-requis : alice@example.com existe en DB
        doNothing().when(appUserService).addFriendByEmail("alice@example.com");

        mockMvc.perform(post("/relation")
                        .with(csrf())
                        .param("email", "alice@example.com"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/relation?success"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("POST /relation avec exception inattendue → redirige avec message générique")
    void postRelation_unexpectedException_shouldRedirectWithError() throws Exception {
        Mockito.doThrow(new RuntimeException("Erreur inattendue"))
                .when(appUserService).addFriendByEmail("bob@example.com");

        mockMvc.perform(post("/relation")
                        .with(csrf())
                        .param("email", "bob@example.com"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/relation?success"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("POST /relation quand utilisateur introuvable → redirige avec erreur sur le champ email")
    void postRelation_userNotFound_shouldRedirectWithBindingError() throws Exception {
        // Stub du service pour lever UserNotFoundException
        Mockito.doThrow(new UserNotFoundException("Utilisateur introuvable"))
                .when(appUserService).addFriendByEmail("inconnu@example.com");

        mockMvc.perform(post("/relation")
                        .with(csrf())
                        .param("email", "inconnu@example.com"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/relation")) // correspond au redirect du catch
                .andExpect(flash().attributeExists("relationDto"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.relationDto"));
    }

    @Test
    @DisplayName("POST /relation avec doublon ou auto-ajout → redirige avec erreur sur le champ email")
    void postRelation_illegalStateOrArgument_shouldRedirectWithBindingError() throws Exception {
        // Stub du service pour lever une exception logique
        Mockito.doThrow(new IllegalArgumentException("Impossible d'ajouter cet utilisateur"))
                .when(appUserService).addFriendByEmail("doublon@example.com");

        mockMvc.perform(post("/relation")
                        .with(csrf())
                        .param("email", "doublon@example.com"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/relation")) // correspond au redirect du catch
                .andExpect(flash().attributeExists("relationDto"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.relationDto"));
    }


}
