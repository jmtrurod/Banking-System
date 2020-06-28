package com.ironhack.BankSystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ironhack.BankSystem.controller.impl.GeneralControllerImpl;
import com.ironhack.BankSystem.model.dto.AccountDto;
import com.ironhack.BankSystem.model.misc.Address;
import com.ironhack.BankSystem.model.misc.Money;
import com.ironhack.BankSystem.model.user.AccountHolder;
import com.ironhack.BankSystem.repository.AccountRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AdminCreditDebitTest {
    @Autowired
    GeneralControllerImpl generalControllerImpl;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountRepository accountRepository;
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
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Attempt of credit: Right Credit. No Content status Expected")
    void credit1() throws Exception {
        Money balance = new Money(new BigDecimal("70"));
        long accountId = accountRepository.findAll().get(0).getId();

        mockMvc.perform(post("/credit/"+ accountId)
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(balance))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertEquals(new BigDecimal("6570.00"), accountRepository.findAll().get(0).getBalance().getAmount());
    }

    @Test
    @DisplayName("Attempt of credit: Negative Money. Bad Request Expected")
    void credit2() throws Exception {
        Money balance = new Money(new BigDecimal("-70"));
        long accountId = accountRepository.findAll().get(0).getId();

        mockMvc.perform(post("/credit/"+ accountId)
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(balance))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertEquals(new BigDecimal("6500.00"), accountRepository.findAll().get(0).getBalance().getAmount());
    }

    @Test
    @DisplayName("Attempt of credit: Non-existing Id. Bad Request Expected")
    void credit3() throws Exception {
        Money balance = new Money(new BigDecimal("-70"));
        long accountId = accountRepository.findAll().get(0).getId() + 190;

        mockMvc.perform(post("/credit/"+ accountId)
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(balance))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertEquals(new BigDecimal("6500.00"), accountRepository.findAll().get(0).getBalance().getAmount());
    }

    @Test
    @DisplayName("Attempt of debit: Right Credit. No Content status Expected")
    void debit1() throws Exception {
        Money balance = new Money(new BigDecimal("70"));
        long accountId = accountRepository.findAll().get(0).getId();

        mockMvc.perform(post("/debit/"+ accountId)
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(balance))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertEquals(new BigDecimal("6430.00"), accountRepository.findAll().get(0).getBalance().getAmount());
    }

    @Test
    @DisplayName("Attempt of debit: Negative Money. Bad Request Expected")
    void debit2() throws Exception {
        Money balance = new Money(new BigDecimal("-70"));
        long accountId = accountRepository.findAll().get(0).getId();

        mockMvc.perform(post("/debit/"+ accountId)
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(balance))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertEquals(new BigDecimal("6500.00"), accountRepository.findAll().get(0).getBalance().getAmount());
    }

    @Test
    @DisplayName("Attempt of debit: Non-existing Id. Bad Request Expected")
    void debit3() throws Exception {
        Money balance = new Money(new BigDecimal("-70"));
        long accountId = accountRepository.findAll().get(0).getId() + 190;

        mockMvc.perform(post("/debit/"+ accountId)
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(balance))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertEquals(new BigDecimal("6500.00"), accountRepository.findAll().get(0).getBalance().getAmount());
    }

}
