package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class LoginControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void showLoginForm_shouldReturnLoginView() throws Exception {

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));// la vue doit être login.html

    }

    @Test
    public void showLoginForm_withErrorInSession_shouldAddErrorToModel() throws Exception {
        mockMvc.perform(get("/login")
                .sessionAttr("error", "Identifiants invalides!"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("error", "Identifiants invalides!"));
    }

    @Test
    public void showLoginForm_withLogoutParam_shouldAddSuccessMessageToModel() throws Exception {
        mockMvc.perform(get("/login").param("logout", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("success", "Déconnexion réussie !"));
    }


    @Test
    void givenExistingUser_whenLogin_thenAuthenticatedAndRedirectedToTransfer() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "alice@example.com")   // username = email
                        .param("password", "Password123a!") // doit matcher le BCrypt stocké
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfer"));
    }

    @Test
    void givenExistingUserWithWrongPassword_whenLogin_thenRedirectToLoginWithErrorInSession() throws Exception {

            mockMvc.perform(post("/login")
                        .param("username", "bob@example.com") // utilisateur existant
                        .param("password", "WrongPassword") // mot de passe incorrect
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void givenNonExistingUser_whenLogin_thenRedirectToLoginWithError() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "nonexistent@mail.com") // email qui n’existe pas
                        .param("password", "AnyPassword123!")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

}
