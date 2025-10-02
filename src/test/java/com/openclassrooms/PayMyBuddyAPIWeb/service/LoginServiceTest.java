package com.openclassrooms.PayMyBuddyAPIWeb.service;

import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import com.openclassrooms.PayMyBuddyAPIWeb.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class LoginServiceTest {
    private AppUserRepository appUserRepository;
    private PasswordEncoder passwordEncoder;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        appUserRepository = mock(AppUserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        loginService = new LoginService(appUserRepository, passwordEncoder);
    }

    @Test
    void authenticate_ShouldReturnTrue_WhenEmailExistsAndPasswordMatches() {
        String email = "user@example.com";
        String rawPassword = "password123";
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setPassword("$2a$10$hash"); // hash fictif

        when(appUserRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, user.getPassword())).thenReturn(true);

        assertTrue(loginService.authenticate(email, rawPassword));

        verify(appUserRepository).findByEmail(email);
        verify(passwordEncoder).matches(rawPassword, user.getPassword());
    }

    @Test
    void authenticate_ShouldReturnFalse_WhenEmailNotFound() {
        String email = "unknown@example.com";
        String password = "password123";

        when(appUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertFalse(loginService.authenticate(email, password));

        verify(appUserRepository).findByEmail(email);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void authenticate_ShouldReturnFalse_WhenPasswordDoesNotMatch() {
        String email = "user@example.com";
        String rawPassword = "wrong-password";
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setPassword("$2a$10$hash");

        when(appUserRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, user.getPassword())).thenReturn(false);

        assertFalse(loginService.authenticate(email, rawPassword));

        verify(appUserRepository).findByEmail(email);
        verify(passwordEncoder).matches(rawPassword, user.getPassword());
    }


}
