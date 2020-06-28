package com.ironhack.BankSystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ironhack.BankSystem.controller.impl.GeneralControllerImpl;
import com.ironhack.BankSystem.model.dto.AccountDto;
import com.ironhack.BankSystem.model.dto.ThirdPartyDto;
import com.ironhack.BankSystem.model.dto.ThirdPartyTransactionDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ThirdPartyControllerImplTest {
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

        ThirdPartyDto thirdPartyDto = new ThirdPartyDto();
        thirdPartyDto.setHashKey("hashkey");
        thirdPartyDto.setPassword("pedro");
        thirdPartyDto.setUsername("pedro");

        mockMvc.perform(post("/third-party")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(thirdPartyDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Attempt of Third Party creation: Missing Data. Bad Request Expected")
    void createThirdParty1() throws Exception {
        ThirdPartyDto thirdPartyDto = new ThirdPartyDto();
        thirdPartyDto.setHashKey("hashkey");
        thirdPartyDto.setPassword("maria");

        mockMvc.perform(post("/third-party")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(thirdPartyDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt of Third Party creation: Already Existing username. Bad Request Expected")
    void createThirdParty2() throws Exception {
        ThirdPartyDto thirdPartyDto = new ThirdPartyDto();
        thirdPartyDto.setHashKey("hashkey");
        thirdPartyDto.setPassword("JUAN2");
        thirdPartyDto.setUsername("JUAN");

        mockMvc.perform(post("/third-party")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(thirdPartyDto))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/third-party")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(thirdPartyDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt of Third Party creation: Right Data. Created status Expected")
    void createThirdParty3() throws Exception {
        ThirdPartyDto thirdPartyDto = new ThirdPartyDto();
        thirdPartyDto.setHashKey("hashkey");
        thirdPartyDto.setPassword("maria");
        thirdPartyDto.setUsername("maria");

        mockMvc.perform(post("/third-party")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(thirdPartyDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Attempt to debit from Third Party: Wrong Secret Key. Bad Request Expected")
    void debitFromThirdParty1() throws Exception {
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

        ThirdPartyTransactionDto dto = new ThirdPartyTransactionDto();
        dto.setAccountId(accountRepository.findAll().get(0).getId());
        dto.setAmount(new BigDecimal(2500));
        dto.setSecretKey("PEPE");

        mockMvc.perform(post("/third-party/debit/WRONGhashkey")
                .with(httpBasic("pedro", "pedro"))
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to debit from Third Party: Wrong Hash Key. Bad Request Expected")
    void debitFromThirdParty2() throws Exception {
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

        ThirdPartyTransactionDto dto = new ThirdPartyTransactionDto();
        dto.setAccountId(accountRepository.findAll().get(0).getId());
        dto.setAmount(new BigDecimal(2500));
        dto.setSecretKey("PEDRO");

        mockMvc.perform(post("/third-party/debit/WRONGhashkey")
                .with(httpBasic("pedro", "pedro"))
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to debit from Third Party: Right Data. Ok Status Expected")
    void debitFromThirdParty3() throws Exception {
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

        ThirdPartyTransactionDto dto = new ThirdPartyTransactionDto();
        dto.setAccountId(accountRepository.findAll().get(0).getId());
        dto.setAmount(new BigDecimal(2500));
        dto.setSecretKey("PEDRO");

        mockMvc.perform(post("/third-party/debit/hashkey")
                .with(httpBasic("pedro", "pedro"))
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Attempt to debit from Third Party: Non-existing Account Id. Bad Request Expected")
    void debitFromThirdParty4() throws Exception {
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

        ThirdPartyTransactionDto dto = new ThirdPartyTransactionDto();
        dto.setAccountId(accountRepository.findAll().get(0).getId()-1);
        dto.setAmount(new BigDecimal(2500));
        dto.setSecretKey("PEDRO");

        mockMvc.perform(post("/third-party/debit/hashkey")
                .with(httpBasic("pedro", "pedro"))
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to debit from Third Party: Wrong Type - Credit Card. Bad Request Expected")
    void debitFromThirdParty5() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("CREDITCARD");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(2006,1,20), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
        accountDto.setPrimaryOwner(accountHolder);
        accountDto.setUsername("PEPE");
        accountDto.setPassword("PEPE");

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON));

        ThirdPartyTransactionDto dto = new ThirdPartyTransactionDto();
        dto.setAccountId(accountRepository.findAll().get(0).getId());
        dto.setAmount(new BigDecimal(2500));
        dto.setSecretKey("PEDRO");

        mockMvc.perform(post("/third-party/debit/hashkey")
                .with(httpBasic("pedro", "pedro"))
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to debit from Third Party: Negative Money. Bad Request Expected")
    void debitFromThirdParty6() throws Exception {
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

        ThirdPartyTransactionDto dto = new ThirdPartyTransactionDto();
        dto.setAccountId(accountRepository.findAll().get(0).getId());
        dto.setAmount(new BigDecimal(-2500));
        dto.setSecretKey("PEDRO");

        mockMvc.perform(post("/third-party/debit/hashkey")
                .with(httpBasic("pedro", "pedro"))
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Attempt to credit from Third Party: Wrong Secret Key. Bad Request Expected")
    void creditFromThirdParty1() throws Exception {
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

        ThirdPartyTransactionDto dto = new ThirdPartyTransactionDto();
        dto.setAccountId(accountRepository.findAll().get(0).getId());
        dto.setAmount(new BigDecimal(2500));
        dto.setSecretKey("PEPE");

        mockMvc.perform(post("/third-party/credit/WRONGhashkey")
                .with(httpBasic("pedro", "pedro"))
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to credit from Third Party: Wrong Hash Key. Bad Request Expected")
    void creditFromThirdParty2() throws Exception {
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

        ThirdPartyTransactionDto dto = new ThirdPartyTransactionDto();
        dto.setAccountId(accountRepository.findAll().get(0).getId());
        dto.setAmount(new BigDecimal(2500));
        dto.setSecretKey("PEDRO");

        mockMvc.perform(post("/third-party/credit/WRONGhashkey")
                .with(httpBasic("pedro", "pedro"))
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to credit from Third Party: Right Data. Ok Status Expected")
    void creditFromThirdParty3() throws Exception {
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

        ThirdPartyTransactionDto dto = new ThirdPartyTransactionDto();
        dto.setAccountId(accountRepository.findAll().get(0).getId());
        dto.setAmount(new BigDecimal(2500));
        dto.setSecretKey("PEDRO");

        mockMvc.perform(post("/third-party/credit/hashkey")
                .with(httpBasic("pedro", "pedro"))
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Attempt to credit from Third Party: Non-existing Account Id. Bad Request Expected")
    void creditFromThirdParty4() throws Exception {
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

        ThirdPartyTransactionDto dto = new ThirdPartyTransactionDto();
        dto.setAccountId(accountRepository.findAll().get(0).getId()-1);
        dto.setAmount(new BigDecimal(2500));
        dto.setSecretKey("PEDRO");

        mockMvc.perform(post("/third-party/credit/hashkey")
                .with(httpBasic("pedro", "pedro"))
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to credit from Third Party: Wrong Type - Credit Card. Bad Request Expected")
    void creditFromThirdParty5() throws Exception {
        AccountDto accountDto = new AccountDto();

        accountDto.setBalance(new BigDecimal("6500"));
        accountDto.setType("CREDITCARD");
        AccountHolder accountHolder = new AccountHolder("Pepe Trujillo", LocalDate.of(2006,1,20), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"), new Address("Pamplona", "Spain", "MyStreet", 10, "85601"));
        accountDto.setPrimaryOwner(accountHolder);
        accountDto.setUsername("PEPE");
        accountDto.setPassword("PEPE");

        mockMvc.perform(post("/account")
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON));

        ThirdPartyTransactionDto dto = new ThirdPartyTransactionDto();
        dto.setAccountId(accountRepository.findAll().get(0).getId());
        dto.setAmount(new BigDecimal(2500));
        dto.setSecretKey("PEDRO");

        mockMvc.perform(post("/third-party/credit/hashkey")
                .with(httpBasic("pedro", "pedro"))
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to credit from Third Party: Negative Money. Bad Request Expected")
    void creditFromThirdParty6() throws Exception {
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

        ThirdPartyTransactionDto dto = new ThirdPartyTransactionDto();
        dto.setAccountId(accountRepository.findAll().get(0).getId());
        dto.setAmount(new BigDecimal(-2500));
        dto.setSecretKey("PEDRO");

        mockMvc.perform(post("/third-party/credit/hashkey")
                .with(httpBasic("pedro", "pedro"))
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


}
