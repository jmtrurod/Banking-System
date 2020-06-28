package com.ironhack.BankSystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ironhack.BankSystem.enums.Status;
import com.ironhack.BankSystem.model.account.Account;
import com.ironhack.BankSystem.model.account.Checking;
import com.ironhack.BankSystem.model.account.CreditCard;
import com.ironhack.BankSystem.model.account.Saving;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
class UpdateAccountTest {

    @Test
    @DisplayName("6 Months Monthly Maintenance Fee applied to Checking Account")
    void checkingUpdate1(){
        Checking checking = new Checking(new Money(new BigDecimal("1000")),"secretkey",new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9),new Address("Madrid", "Spain", "MyStreet", 10, "41987"), new Address("Algeciras", "Spain", "AnotherStreet", 12, "34512")), null);
        checking.setReferenceInstant(LocalDateTime.now().minusMonths(6));
        checking.update();
        assertEquals(new BigDecimal("928.00"), checking.getBalance().getAmount());
        assertEquals(0,checking.getReferenceInstant().until(LocalDateTime.now(), ChronoUnit.MONTHS));
    }

    @Test
    @DisplayName("10 Months Monthly Maintenance Fee plus Penalty applied to Checking Account")
    void checkingUpdate2(){
        Checking checking = new Checking(new Money(new BigDecimal("200")),"secretkey",new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9),new Address("Madrid", "Spain", "MyStreet", 10, "41987"), new Address("Algeciras", "Spain", "AnotherStreet", 12, "34512")), null);
        checking.setReferenceInstant(LocalDateTime.now().minusMonths(10));
        checking.update();
        assertEquals(new BigDecimal("40.00"), checking.getBalance().getAmount());
    }

    @Test
    @DisplayName("1 Month Interest Rate applied to Credit Card Account")
    void creditCardUpdate1(){
        CreditCard creditCard = new CreditCard(new Money(new BigDecimal("100000")),new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9),new Address("Madrid", "Spain", "MyStreet", 10, "41987"), new Address("Algeciras", "Spain", "AnotherStreet", 12, "34512")), null, new BigDecimal(100), new BigDecimal("0.12"));
        creditCard.setReferenceInstant(LocalDateTime.now().minusMonths(1));
        creditCard.update();
        assertEquals(new BigDecimal("101000.00"), creditCard.getBalance().getAmount());
    }

    @Test
    @DisplayName("1 Year Interest Rate applied to Saving Account")
    void savingUpdate1(){
        Saving saving = new Saving(new Money(new BigDecimal("1000")), "secretkey", new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9),new Address("Madrid", "Spain", "MyStreet", 10, "41987"), new Address("Algeciras", "Spain", "AnotherStreet", 12, "34512")), null, new BigDecimal(100), new BigDecimal("0.12"));
        saving.setReferenceInstant(LocalDateTime.now().minusMonths(13));
        saving.update();
        assertEquals(new BigDecimal("1120.00"), saving.getBalance().getAmount());
    }

    @Test
    @DisplayName("Penalty applied for sub-passing Minimum Balance in Saving Account")
    void savingUpdate2(){
        Saving saving = new Saving(new Money(new BigDecimal("1000")), "secretkey", new AccountHolder("Pepe Trujillo", LocalDate.of(1996,3,9),new Address("Madrid", "Spain", "MyStreet", 10, "41987"), new Address("Algeciras", "Spain", "AnotherStreet", 12, "34512")), null, new BigDecimal(100), new BigDecimal("0.12"));
        saving.setBalance(new Money(new BigDecimal("50")));
        saving.update();
        assertEquals(new BigDecimal("10.00"), saving.getBalance().getAmount());
    }

}
