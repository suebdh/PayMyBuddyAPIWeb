package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.RegisterDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.repository.AppUserRepository;
import com.openclassrooms.PayMyBuddyAPIWeb.service.AppUserService;
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
public class RegisterControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserService appUserService;// version réelle du service

    @Autowired
    private AppUserRepository appUserRepository;


    @Test
    public void showRegisterForm_shouldReturnRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerDTO"));
    }

    @Test
    public void registerUser_withValidData_shouldRedirectToLoginView() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("userName", "testUserName")
                        .param("email", "testemail@example.com")
                        .param("password", "Password123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        //Mockito.verify(appUserService).createUser(Mockito.any(RegisterDTO.class));
        // Vérifie que l'utilisateur a bien été créé
        assert(appUserRepository.findByEmail("testemail@example.com").isPresent());
    }

    @Test
    public void registerUser_withInvalidData_shouldReturnRegisterView() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("userName", "")
                        .param("email", "adresse-email-sans-arobase")
                        .param("password", "Password"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("registerDTO", "userName", "email", "password"));
    }

    @Test
    public void registerUser_wthEmailAlreadyUsed_shouldReturnRegisterViewWithErrors() throws Exception {

        // Crée déjà un utilisateur avec cet email
        appUserService.createUser(new RegisterDTO("user1", "duplicate@example.com", "Password123!"));


        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("userName", "newUser")
                        .param("email", "duplicate@example.com")
                        .param("password", "Password123!"))
                .andExpect(status().isOk()) // reste sur la vue register (pas de redirection)
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("registerDTO", "email")); // erreur sur le champ email

    }

    @Test
    public void registerUser_withUsernameAlreadyUsed_shouldReturnRegisterViewWithErrors() throws Exception {

        // Crée déjà un utilisateur avec ce username
        appUserService.createUser(new RegisterDTO("existingUser", "user1@example.com", "Password123!"));


        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("userName", "existingUser")
                        .param("email", "newemail@example.com")
                        .param("password", "Password123!"))
                .andExpect(status().isOk()) // reste sur la vue register
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("registerDTO", "userName")); // erreur sur le champ userName
    }

}
