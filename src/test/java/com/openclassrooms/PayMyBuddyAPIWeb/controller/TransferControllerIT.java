package com.openclassrooms.PayMyBuddyAPIWeb.controller;

import com.openclassrooms.PayMyBuddyAPIWeb.dto.TransferHistoryDTO;
import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppTransaction;
import com.openclassrooms.PayMyBuddyAPIWeb.entity.AppUser;
import com.openclassrooms.PayMyBuddyAPIWeb.repository.AppTransactionRepository;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Autowired
    private AppTransactionRepository appTransactionRepository;

    @BeforeEach
    void setup() {

        // Vider les tables pour éviter les doublons
        appTransactionRepository.deleteAll();
        appUserRepository.deleteAll();

        // créer l'utilisateur courant
        AppUser currentUser = new AppUser();
        currentUser.setUserName("testuser");
        currentUser.setPassword("Sst5892s!ST");
        currentUser.setBalance(BigDecimal.ZERO);
        currentUser.setBalance(BigDecimal.valueOf(5000));
        currentUser.setEmail("currentuser@example.com");
        appUserRepository.save(currentUser);

        // créer les amis
        AppUser friend1 = new AppUser();
        friend1.setUserName("amiTest1");
        friend1.setBalance(BigDecimal.ZERO);
        friend1.setPassword("Set5892s?sE");
        friend1.setEmail("friend1@example.com");
        appUserRepository.save(friend1);

        AppUser friend2 = new AppUser();
        friend2.setUserName("amiTest2");
        friend2.setBalance(BigDecimal.ZERO);
        friend2.setPassword("Fet5892s?sF");
        friend2.setEmail("friend2@example.com");
        appUserRepository.save(friend2);

        // Ajouter les amis à l'utilisateur courant
        currentUser.addFriend(friend1);
        currentUser.addFriend(friend2);
        appUserRepository.save(currentUser); // persister la relation dans la table de jointure

        // friend1 envoie de l'argent à currentUser
        AppTransaction tx1 = new AppTransaction();
        tx1.setSender(friend1);
        tx1.setReceiver(currentUser);
        tx1.setDescription("Cadeau");
        tx1.setAmountTransaction(BigDecimal.valueOf(20));
        currentUser.getReceivedTransactions().add(tx1);
        friend1.getSentTransactions().add(tx1);

        // currentUser envoie à friend2
        AppTransaction tx2 = new AppTransaction();
        tx2.setSender(currentUser);
        tx2.setReceiver(friend2);
        tx2.setDescription("Repas");
        tx2.setAmountTransaction(BigDecimal.valueOf(15.00));
        currentUser.getSentTransactions().add(tx2);
        friend2.getReceivedTransactions().add(tx2);

        //Sauvegarder ces transactions
        appTransactionRepository.save(tx1);
        appTransactionRepository.save(tx2);
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


                    // Vérifier l'historique des transactions
                    List<TransferHistoryDTO> transactions = (List<TransferHistoryDTO>) mvcResult.getModelAndView().getModel().get("transactions");

                    TransferHistoryDTO sentTx = transactions.stream()
                            .filter(tx -> tx.getRelation().equals("amiTest2"))
                            .findFirst().orElseThrow();
                    //assertEquals(BigDecimal.valueOf(-15.00), sentTx.getMontant());
                    assertEquals(0, sentTx.getMontant().compareTo(BigDecimal.valueOf(-15)));

                    TransferHistoryDTO receivedTx = transactions.stream()
                            .filter(tx -> tx.getRelation().equals("amiTest1"))
                            .findFirst().orElseThrow();
                    //assertEquals(BigDecimal.valueOf(20.00), receivedTx.getMontant());
                    assertEquals(0, receivedTx.getMontant().compareTo(BigDecimal.valueOf(20)));
                });
    }

    @Test
    @WithMockUser(username = "currentuser@example.com")
    void handleTransfer_shouldReturnTransferView_whenValidationErrors() throws Exception {
        mockMvc.perform(post("/transfer")
                        .param("relation", "")  // vide, erreur de validation
                        .param("description", "Test transfert")
                        .param("montant", "")  // vide, erreur de validation
                .with(csrf())) // <-- IMPORTANT
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().attributeExists("friends"))
                .andExpect(model().attributeExists("transactions"));
    }

    @Test
    @WithMockUser(username = "currentuser@example.com")
    void handleTransfer_shouldRedirectToTransferWithSuccessMessage_whenTransferOk() throws Exception {
        mockMvc.perform(post("/transfer")
                        .param("relation", "amiTest2")  // relation existante
                        .param("description", "Test transfert")
                        .param("montant", "10.00")      // montant valide
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfer"))
                .andExpect(flash().attribute("successMessage", "Transfert d'argent effectué avec succès."));
    }

    // Test pour IllegalArgumentException
    @Test
    @WithMockUser(username = "currentuser@example.com")
    void handleTransfer_shouldReturnTransferView_whenIllegalArgumentException() throws Exception {

        mockMvc.perform(post("/transfer")
                        .param("relation", "amiTest2")
                        .param("description", "Test")
                        .param("montant", "10000") // montant supérieur au solde du currentUser
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attributeExists("friends"))
                .andExpect(model().attributeExists("transactions"));
    }

    //Tester le transfert vers soi-même
    @Test
    @WithMockUser(username = "currentuser@example.com")
    void handleTransfer_shouldReturnError_whenTransferToSelf() throws Exception {
        mockMvc.perform(post("/transfer")
                        .param("relation", "testuser") // nom d'utilisateur courant
                        .param("description", "Test transfert")
                        .param("montant", "10")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attributeExists("friends"))
                .andExpect(model().attributeExists("transactions"));
    }

}
