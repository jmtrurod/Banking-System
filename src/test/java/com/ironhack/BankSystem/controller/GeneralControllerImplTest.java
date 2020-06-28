package com.ironhack.BankSystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ironhack.BankSystem.controller.impl.GeneralControllerImpl;
import com.ironhack.BankSystem.enums.Status;
import com.ironhack.BankSystem.model.account.Account;
import com.ironhack.BankSystem.model.dto.AccountDto;
import com.ironhack.BankSystem.model.misc.Address;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class GeneralControllerImplTest {
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
    void setUp() {
        mockMvc =  MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Attempt to create Account without Primary Owner or Primary Owner Id. Bad Request Expected")
    void createAccount1() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setMinimumBalance(new BigDecimal("-30"));
        accountDto.setInterestRate(new BigDecimal("18"));
        accountDto.setSecretKey("key");
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setCreditLimit(new BigDecimal("-65"));
        accountDto.setType("INVENTED");

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to create Account with non-existing Primary Owner Id. Bad Request Expected")
    void createAccount2() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setMinimumBalance(new BigDecimal("-30"));
        accountDto.setInterestRate(new BigDecimal("18"));
        accountDto.setSecretKey("key");
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setCreditLimit(new BigDecimal("-65"));
        accountDto.setType("INVENTED");
        accountDto.setPrimaryOwnerId(300);

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to create Account Primary Owner Data without username and password. Bad Request Expected")
    void createAccount3() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setMinimumBalance(new BigDecimal("-30"));
        accountDto.setInterestRate(new BigDecimal("18"));
        accountDto.setSecretKey("key");
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setCreditLimit(new BigDecimal("-65"));
        accountDto.setType("INVENTED");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
        accountDto.setPrimaryOwner(accountHolder);

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to create Account with Primary Owner Data and Primary Owner Id. Bad Request Expected")
    void createAccount4() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setMinimumBalance(new BigDecimal("-30"));
        accountDto.setInterestRate(new BigDecimal("18"));
        accountDto.setSecretKey("key");
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setCreditLimit(new BigDecimal("-65"));
        accountDto.setType("INVENTED");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
        accountDto.setPrimaryOwner(accountHolder);
        accountDto.setUsername("PEPE");
        accountDto.setPassword("PEPE");
        accountDto.setPrimaryOwnerId(5);

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to create Account with Secondary Owner Data and Secondary Owner Id. Bad Request Expected")
    void createAccount5() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setMinimumBalance(new BigDecimal("-30"));
        accountDto.setInterestRate(new BigDecimal("18"));
        accountDto.setSecretKey("key");
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setCreditLimit(new BigDecimal("-65"));
        accountDto.setType("INVENTED");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
        accountDto.setPrimaryOwner(accountHolder);
        accountDto.setSecondaryOwner(accountHolder);
        accountDto.setUsername("PEPE");
        accountDto.setPassword("PEPE");
        accountDto.setSecondaryOwnerId(20);
        accountDto.setSecondaryUsername("JUAN");
        accountDto.setSecondaryPassword("JUAN");

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to create Account with non-existing Secondary Owner Id. Bad Request Expected")
    void createAccount6() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setMinimumBalance(new BigDecimal("-30"));
        accountDto.setInterestRate(new BigDecimal("18"));
        accountDto.setSecretKey("key");
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setCreditLimit(new BigDecimal("-65"));
        accountDto.setType("INVENTED");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
        accountDto.setPrimaryOwner(accountHolder);
        accountDto.setUsername("PEPE");
        accountDto.setPassword("PEPE");
        accountDto.setSecondaryOwnerId(20);

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to create Account with Secondary Owner Data without username or password. Bad Request Expected")
    void createAccount7() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setMinimumBalance(new BigDecimal("-30"));
        accountDto.setInterestRate(new BigDecimal("18"));
        accountDto.setSecretKey("key");
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setCreditLimit(new BigDecimal("-65"));
        accountDto.setType("INVENTED");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
        accountDto.setPrimaryOwner(accountHolder);
        accountDto.setSecondaryOwner(accountHolder);
        accountDto.setUsername("PEPE");
        accountDto.setPassword("PEPE");

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to create Account providing wrong Type. Bad Request Expected")
    void createAccount8() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setMinimumBalance(new BigDecimal("-30"));
        accountDto.setInterestRate(new BigDecimal("18"));
        accountDto.setSecretKey("key");
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setCreditLimit(new BigDecimal("-65"));
        accountDto.setType("INVENTED");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
        accountDto.setPrimaryOwner(accountHolder);
        accountDto.setUsername("PEPE");
        accountDto.setPassword("PEPE");

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to create Checking Account a negative balance. Bad Request Expected")
    void createAccount9() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setSecretKey("key");
        accountDto.setBalance(new BigDecimal("-6500"));
        accountDto.setType("CHECKING");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
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
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to create Account providing Duplicated Usernames (no account with id). Bad Request Expected")
    void createCheckingAccount10() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setSecretKey("PEPE");
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("CHECKING");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
        accountDto.setPrimaryOwner(accountHolder);
        accountDto.setUsername("PEPE");
        accountDto.setPassword("PEPE");
        accountDto.setSecondaryOwner(accountHolder);
        accountDto.setSecondaryUsername("PEPE");
        accountDto.setSecondaryPassword("JUAN");

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to create Checking Account providing Missing Data. Bad Request Expected")
    void createCheckingAccount1() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("CHECKING");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
        accountDto.setPrimaryOwner(accountHolder);
        accountDto.setUsername("PEPE");
        accountDto.setPassword("PEPE");

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to create Checking Account providing Extra Data. Bad Request Expected")
    void createCheckingAccount2() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setSecretKey("key");
        accountDto.setCreditLimit(new BigDecimal("125"));
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("CHECKING");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
        accountDto.setPrimaryOwner(accountHolder);
        accountDto.setUsername("PEPE");
        accountDto.setPassword("PEPE");

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to create Checking Account providing Right Data (no account with id). Created status Expected")
    void createCheckingAccount3() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setSecretKey("PEPE");
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("CHECKING");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
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
    }

    @Test
    @DisplayName("Attempt to create Credit Card Account providing Extra Data. Bad Request Expected")
    void createCreditCard1() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setSecretKey("key");
        accountDto.setCreditLimit(new BigDecimal("150"));
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("CREDITCARD");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
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
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to create Credit Card Account providing Wrong Interest Rate. Bad Request Expected")
    void createCreditCard2() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setInterestRate(new BigDecimal("30"));
        accountDto.setCreditLimit(new BigDecimal("150"));
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("CREDITCARD");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
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
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to create Credit Card Account providing Wrong Credit Limit. Bad Request Expected")
    void createCreditCard3() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setCreditLimit(new BigDecimal("50"));
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("CREDITCARD");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
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
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to create Credit Card Account providing Right Data. Created status Expected")
    void createCreditCard4() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("CREDITCARD");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
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
    }

    @Test
    @DisplayName("Attempt to create SavingAccount providing Missing Data. Bad Request Expected")
    void createSaving1() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("SAVING");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
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
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to create SavingAccount providing Extra Data. Bad Request Expected")
    void createSaving2() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setSecretKey("key");
        accountDto.setCreditLimit(new BigDecimal("150"));
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("SAVING");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
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
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to create Saving Account providing Wrong Interest Rate. Bad Request Expected")
    void createSaving3() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setSecretKey("key");
        accountDto.setInterestRate(new BigDecimal("0.6"));
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("SAVING");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
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
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to create Saving Account providing Wrong Minimum Balance. Bad Request Expected")
    void createSaving4() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setSecretKey("key");
        accountDto.setMinimumBalance(new BigDecimal("50"));
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("SAVING");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
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
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to create Saving Account providing Right Data. Created status Expected")
    void createSaving5() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setSecretKey("key");
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("SAVING");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
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
    }

    @Test
    @DisplayName("Attempt to create Student Checking Account providing Missing Data. Bad Request Expected")
    void createStudentCheckingAccount1() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("STUDENT");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
        accountDto.setPrimaryOwner(accountHolder);
        accountDto.setUsername("PEPE");
        accountDto.setPassword("PEPE");

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to create Student Checking Account providing Extra Data. Bad Request Expected")
    void createStudentCheckingAccount2() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setSecretKey("key");
        accountDto.setCreditLimit(new BigDecimal("125"));
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("STUDENT");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
        accountDto.setPrimaryOwner(accountHolder);
        accountDto.setUsername("PEPE");
        accountDto.setPassword("PEPE");

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to create Student Checking Account providing Right Data (no account with id). Created status Expected")
    void createStudentCheckingAccount3() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setSecretKey("PEPE");
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("STUDENT");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
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
    }

    @Test
    @DisplayName("Attempt to create Student Checking Account providing Right Data (no account with id) through Checking Account. Created status Expected")
    void createStudentCheckingAccount4() throws Exception {
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
    }

    @Test
    @DisplayName("Account Holder attempting to access to his account data")
    void getAccount1() throws Exception {
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

        mockMvc.perform(get("/account/" + accountRepository.findAll().get(0).getId())
                .with(httpBasic("JUAN", "JUAN")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Not allowed Account Holder attempting to access to an account data")
    void getAccount2() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setSecretKey("PEDRO");
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("STUDENT");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(2006,1,20), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
        accountDto.setPrimaryOwner(accountHolder);
        accountDto.setUsername("PEPE");
        accountDto.setPassword("PEPE");

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON));

        accountDto.setUsername("JUAN");
        accountDto.setPassword("JUAN");

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/account/" + accountRepository.findAll().get(0).getId())
                .with(httpBasic("JUAN", "JUAN")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Account Holder attempting to access to a non-existing account")
    void getAccount3() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setSecretKey("PEDRO");
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("STUDENT");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(2006,1,20), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
        accountDto.setPrimaryOwner(accountHolder);
        accountDto.setUsername("PEPE");
        accountDto.setPassword("PEPE");

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/account/" + (accountRepository.findAll().get(0).getId()-1))
                .with(httpBasic("PEPE", "PEPE")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Admin unfreeze Frozen Account")
    void unfreezeAccount1() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setSecretKey("PEDRO");
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("STUDENT");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(2006,1,20), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
        accountDto.setPrimaryOwner(accountHolder);
        accountDto.setUsername("PEPE");
        accountDto.setPassword("PEPE");

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON));

        Account account = accountRepository.findAll().get(0);
        account.setStatus(Status.FROZEN);
        accountRepository.save(account);

        mockMvc.perform(patch("/account/unfreeze/" + accountRepository.findAll().get(0).getId())
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Admin attempting to unfreeze an Active Account")
    void unfreezeAccount2() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setSecretKey("PEDRO");
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("STUDENT");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(2006,1,20), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
        accountDto.setPrimaryOwner(accountHolder);
        accountDto.setUsername("PEPE");
        accountDto.setPassword("PEPE");

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(patch("/account/unfreeze/" + accountRepository.findAll().get(0).getId())
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Admin attempting to unfreeze a non-existing Account")
    void unfreezeAccount3() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setSecretKey("PEDRO");
        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("STUDENT");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(2006,1,20), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
        accountDto.setPrimaryOwner(accountHolder);
        accountDto.setUsername("PEPE");
        accountDto.setPassword("PEPE");

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(patch("/account/unfreeze/" + (accountRepository.findAll().get(0).getId()-1))
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }
}