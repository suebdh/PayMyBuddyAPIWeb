package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.RelationDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
@WithMockUser // utilisateur connecté par défaut
public class RelationControllerIT {

    @Autowired
    private MockMvc mockMvc;

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
}
