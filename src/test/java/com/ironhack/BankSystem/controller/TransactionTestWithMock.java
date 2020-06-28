package com.ironhack.BankSystem.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ironhack.BankSystem.controller.impl.GeneralControllerImpl;
import com.ironhack.BankSystem.enums.Status;
import com.ironhack.BankSystem.model.account.Account;
import com.ironhack.BankSystem.model.dto.AccountDto;
import com.ironhack.BankSystem.model.misc.Address;
import com.ironhack.BankSystem.model.misc.Money;
import com.ironhack.BankSystem.model.user.AccountHolder;
import com.ironhack.BankSystem.repository.AccountRepository;
import com.ironhack.BankSystem.repository.TransactionRepository;
import com.ironhack.BankSystem.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TransactionTestWithMock {
    @Autowired
    GeneralControllerImpl generalControllerImpl;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountRepository accountRepository;
    @MockBean
    TransactionRepository transactionRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        mockMvc =  MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);

        AccountDto accountDto = new AccountDto();

        accountDto.setSecretKey("PEDRO");
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("STUDENT");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(2006,1,20), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
        accountDto.setPrimaryOwner(accountHolder);
        accountDto.setUsername("PEPE");
        accountDto.setPassword("PEPE");
        accountDto.setSecondaryOwner(accountHolder);
        accountDto.setSecondaryUsername("JUAN");
        accountDto.setSecondaryPassword("JUAN");

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        accountDto.setUsername("MARIPAZ");
        accountDto.setPassword("MARIPAZ");
        accountDto.setSecondaryOwner(null);

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Account account = accountRepository.findAll().get(0);
        account.getMaxTransactionControl().setValue(new BigDecimal("100"));
        accountRepository.save(account);

        List<LocalDateTime> query1 = new ArrayList<LocalDateTime>();
        query1.add(LocalDateTime.of(2020,3,9,17,15,37));
        query1.add(LocalDateTime.of(2020,3,9,18,52,41));
        query1.add(LocalDateTime.now().minusSeconds(150));
        query1.add(LocalDateTime.now().plusSeconds(5));

        List<BigDecimal> query2 = new ArrayList<BigDecimal>();
        query2.add(new BigDecimal(-35));
        query2.add(new BigDecimal(-65));
        query2.add(new BigDecimal(-35));
        query2.add(new BigDecimal(-65));

        when(transactionRepository.instantTransactionsByAccount(accountRepository.findAll().get(0).getId())).thenReturn(query1);
        when(transactionRepository.quantityTransactionsByAccount(accountRepository.findAll().get(0).getId())).thenReturn(query2);
    }

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Attempt of transference: Over 150% of Maximum in less than 24h. Bad Request Expected")
    void transferenceUnitary2() throws Exception {
        Money balance = new Money(new BigDecimal("5000"));
        long idFrom = accountRepository.findAll().get(0).getId();
        long idTo = accountRepository.findAll().get(1).getId();

        mockMvc.perform(post("/transfer/accountFrom/"+ idFrom + "/accountTo/" + idTo)
                .with(httpBasic("PEPE", "PEPE"))
                .content(objectMapper.writeValueAsString(balance))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertEquals(new BigDecimal("6500.00"), accountRepository.findAll().get(0).getBalance().getAmount());
        assertEquals(new BigDecimal("6500.00"), accountRepository.findAll().get(1).getBalance().getAmount());
        assertEquals(Status.FROZEN, accountRepository.findAll().get(0).getStatus());
    }

    @Test
    @DisplayName("Attempt of transference: Two transferences in less than 1 sec")
    void transferenceUnitary3() throws Exception {
        
        List<LocalDateTime> listTime = new ArrayList<LocalDateTime>();
        listTime.add(LocalDateTime.now());

        when(transactionRepository.lastSpent(accountRepository.findAll().get(0).getId())).thenReturn(listTime);

        Money balance = new Money(new BigDecimal("70"));
        long idFrom = accountRepository.findAll().get(0).getId();
        long idTo = accountRepository.findAll().get(1).getId();

        mockMvc.perform(post("/transfer/accountFrom/"+ idFrom + "/accountTo/" + idTo)
                .with(httpBasic("PEPE", "PEPE"))
                .content(objectMapper.writeValueAsString(balance))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertEquals(new BigDecimal("6500.00"), accountRepository.findAll().get(0).getBalance().getAmount());
        assertEquals(new BigDecimal("6500.00"), accountRepository.findAll().get(1).getBalance().getAmount());
        assertEquals(Status.FROZEN, accountRepository.findAll().get(0).getStatus());
    }

}
