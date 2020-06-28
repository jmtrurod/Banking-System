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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TransactionIntegrationTest {
    @Autowired
    GeneralControllerImpl generalControllerImpl;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
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
                .contentType(MediaType.APPLICATION_JSON));

        accountDto.setUsername("MARIPAZ");
        accountDto.setPassword("MARIPAZ");
        accountDto.setSecondaryOwner(null);

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Attempt of transference: Negative Money. Bad Request Expected")
    void transference1() throws Exception {
        Money balance = new Money(new BigDecimal("-500"));
        long idFrom = accountRepository.findAll().get(0).getId();
        long idTo = accountRepository.findAll().get(1).getId();

        System.out.println(userRepository.findAll());

        mockMvc.perform(post("/transfer/accountFrom/"+ idFrom + "/accountTo/" + idTo)
                .with(httpBasic("PEPE", "PEPE"))
                .content(objectMapper.writeValueAsString(balance))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt of transference: Wrong Id-From. Bad Request Expected")
    void transference2() throws Exception {
        Money balance = new Money(new BigDecimal("500"));
        long idFrom = accountRepository.findAll().get(0).getId();
        long idTo = accountRepository.findAll().get(1).getId();

        mockMvc.perform(post("/transfer/accountFrom/"+ idFrom+8 + "/accountTo/" + idTo)
                .with(httpBasic("PEPE", "PEPE"))
                .content(objectMapper.writeValueAsString(balance))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt of transference: Wrong Id-To. Bad Request Expected")
    void transference3() throws Exception {
        Money balance = new Money(new BigDecimal("500"));
        long idFrom = accountRepository.findAll().get(0).getId();
        long idTo = accountRepository.findAll().get(1).getId();

        mockMvc.perform(post("/transfer/accountFrom/"+ idFrom + "/accountTo/" + idTo+8)
                .with(httpBasic("PEPE", "PEPE"))
                .content(objectMapper.writeValueAsString(balance))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt of transference: Wrong Ids. Bad Request Expected")
    void transference4() throws Exception {
        Money balance = new Money(new BigDecimal("500"));
        long idFrom = accountRepository.findAll().get(0).getId();
        long idTo = accountRepository.findAll().get(1).getId();

        mockMvc.perform(post("/transfer/accountFrom/"+ idFrom+8 + "/accountTo/" + idTo+8)
                .with(httpBasic("PEPE", "PEPE"))
                .content(objectMapper.writeValueAsString(balance))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt of transference: Non-allowed Account Holder. Unauthorized Expected")
    void transference5() throws Exception {
        Money balance = new Money(new BigDecimal("500"));
        long idFrom = accountRepository.findAll().get(0).getId();
        long idTo = accountRepository.findAll().get(1).getId();

        mockMvc.perform(post("/transfer/accountFrom/"+ idFrom + "/accountTo/" + idTo)
                .with(httpBasic("MARIPAZ", "MARIPAZ"))
                .content(objectMapper.writeValueAsString(balance))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Attempt of transference: Frozen account. Bad Request Expected")
    void transference6() throws Exception {
        Money balance = new Money(new BigDecimal("500"));
        long idFrom = accountRepository.findAll().get(0).getId();
        long idTo = accountRepository.findAll().get(1).getId();

        Account account = accountRepository.findAll().get(0);
        account.setStatus(Status.FROZEN);
        accountRepository.save(account);

        mockMvc.perform(post("/transfer/accountFrom/"+ idFrom + "/accountTo/" + idTo)
                .with(httpBasic("PEPE", "PEPE"))
                .content(objectMapper.writeValueAsString(balance))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt of transference: Too much money. Bad Request Expected")
    void transference7() throws Exception {
        Money balance = new Money(new BigDecimal("150000"));
        long idFrom = accountRepository.findAll().get(0).getId();
        long idTo = accountRepository.findAll().get(1).getId();

        System.out.println("HI-0");
        mockMvc.perform(post("/transfer/accountFrom/"+ idFrom + "/accountTo/" + idTo)
                .with(httpBasic("PEPE", "PEPE"))
                .content(objectMapper.writeValueAsString(balance))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt of transference: Right Transaction. Created status Expected")
    void transference8() throws Exception {
        Money balance = new Money(new BigDecimal("500"));
        long idFrom = accountRepository.findAll().get(0).getId();
        long idTo = accountRepository.findAll().get(1).getId();

        System.out.println("HI-0");
        mockMvc.perform(post("/transfer/accountFrom/"+ idFrom + "/accountTo/" + idTo)
                .with(httpBasic("PEPE", "PEPE"))
                .content(objectMapper.writeValueAsString(balance))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(new BigDecimal("6000.00"), accountRepository.findAll().get(0).getBalance().getAmount());
        assertEquals(new BigDecimal("7000.00"), accountRepository.findAll().get(1).getBalance().getAmount());
    }

}