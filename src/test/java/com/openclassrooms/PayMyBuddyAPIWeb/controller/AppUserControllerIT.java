package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.PayMyBuddyAPIWeb.dto.AppUserDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class AppUserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    public void testCreateUser_shouldReturnAppUser() throws Exception {
        // given
        AppUserDTO appUserDTO = new AppUserDTO(
                0,
                "sue",
                "sh@yahoo.fr",
                "3GHE53GHE53GHE6",
                new BigDecimal("2000"),
                null);

        // when / then
        mockMvc.perform(post("/api/users")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUserDTO)))
                .andExpect(status().isOk());
             //   .andExpect(jsonPath("$.userName", is("sue")))
               // .andExpect(jsonPath("$.balance", is(2000)));

    }
}
