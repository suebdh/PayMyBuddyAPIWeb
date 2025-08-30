package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
