package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.RegisterDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.EmailAlreadyUsedException;
import com.openclassrooms.PayMyBuddyAPIWeb.exception.UsernameAlreadyUsedException;
import com.openclassrooms.PayMyBuddyAPIWeb.service.AppUserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
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
    private AppUserService appUserService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AppUserService appUserService() {
            return Mockito.mock(AppUserService.class); // mock injecté
        }
    }

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

        Mockito.verify(appUserService).createUser(Mockito.any(RegisterDTO.class));
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

        // Préparer un DTO avec un email déjà utilisé
        String usedEmail = "existing@example.com";

        // Simuler que le service lève une exception EmailAlreadyUsedException
        Mockito.doThrow(new EmailAlreadyUsedException("Email already used"))
                .when(appUserService).createUser(Mockito.any(RegisterDTO.class));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("userName", "newUser")
                        .param("email", usedEmail)
                        .param("password", "Password123!"))
                .andExpect(status().isOk()) // reste sur la vue register (pas de redirection)
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("registerDTO", "email")); // erreur sur le champ email

    }

    @Test
    public void registerUser_withUsernameAlreadyUsed_shouldReturnRegisterViewWithErrors() throws Exception {

        // Préparer un DTO avec un username déjà utilisé
        String usedUsername = "existingUser";

        // Simuler que le service lève une exception UsernameAlreadyUsedException
        Mockito.doThrow(new UsernameAlreadyUsedException("Username already used"))
                .when(appUserService).createUser(Mockito.any(RegisterDTO.class));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("userName", usedUsername)
                        .param("email", "newemail@example.com")
                        .param("password", "Password123!"))
                .andExpect(status().isOk()) // reste sur la vue register
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("registerDTO", "userName")); // erreur sur le champ userName
    }

}
