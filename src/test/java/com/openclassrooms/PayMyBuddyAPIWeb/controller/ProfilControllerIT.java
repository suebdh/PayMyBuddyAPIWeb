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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
