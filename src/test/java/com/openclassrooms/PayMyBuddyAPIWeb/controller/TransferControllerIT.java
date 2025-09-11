package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import com.openclassrooms.PayMyBuddyAPIWeb.repository.AppUserRepository;
import com.openclassrooms.PayMyBuddyAPIWeb.service.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class TransferControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepository appUserRepository;

    private AppUser currentUser;
    private AppUser friend1;
    private AppUser friend2;

    @BeforeEach
    void setup() {

        // créer l'utilisateur courant
        currentUser = new AppUser();
        currentUser.setUserName("testuser");
        currentUser.setPassword("Sst5892s!ST");
        currentUser.setBalance(BigDecimal.ZERO);
        currentUser.setEmail("currentuser@example.com");
        appUserRepository.save(currentUser);

        // créer les amis
        friend1 = new AppUser();
        friend1.setUserName("amiTest1");
        friend1.setBalance(BigDecimal.ZERO);
        friend1.setPassword("Set5892s?sE");
        friend1.setEmail("friend1@example.com");
        appUserRepository.save(friend1);

        friend2 = new AppUser();
        friend2.setUserName("amiTest2");
        friend2.setBalance(BigDecimal.ZERO);
        friend2.setPassword("Fet5892s?sF");
        friend2.setEmail("friend2@example.com");
        appUserRepository.save(friend2);

        // **Ajouter les amis à l'utilisateur courant**
        currentUser.addFriend(friend1);
        currentUser.addFriend(friend2);
        appUserRepository.save(currentUser); // persister la relation dans la table de jointure
    }

    @Test
    @WithMockUser(username = "currentuser@example.com")
        // utilisateur simulé
    void showTransferPage_shouldReturnTransferViewWithModelAttributes() throws Exception {
        mockMvc.perform(get("/transfer"))
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().attributeExists("transferForm"))
                .andExpect(model().attributeExists("friends"))
                .andDo(mvcResult -> {
                    List<AppUser> friendsFromModel = ((List<AppUser>) mvcResult.getModelAndView().getModel().get("friends"))
                            .stream()
                            .sorted(Comparator.comparing(AppUser::getUserName))
                            .toList();

                    // vérifier que la liste contient les amis
                    assertEquals(2, friendsFromModel.size());
                    assertEquals("amiTest1", friendsFromModel.get(0).getUserName());
                    assertEquals("amiTest2", friendsFromModel.get(1).getUserName());
                });
    }
}
