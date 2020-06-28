package com.ironhack.BankSystem.service;

import com.ironhack.BankSystem.exception.ExtraDataNotAllowed;
import com.ironhack.BankSystem.exception.MissingDataInput;
import com.ironhack.BankSystem.exception.WrongDataInputException;
import com.ironhack.BankSystem.model.account.Account;
import com.ironhack.BankSystem.model.account.CreditCard;
import com.ironhack.BankSystem.model.dto.AccountDto;
import com.ironhack.BankSystem.model.misc.Money;
import com.ironhack.BankSystem.model.modelView.AccountMV;
import com.ironhack.BankSystem.model.user.AccountHolder;
import com.ironhack.BankSystem.model.user.Role;
import com.ironhack.BankSystem.model.user.User;
import com.ironhack.BankSystem.repository.AccountRepository;
import com.ironhack.BankSystem.repository.user.AccountHolderRepository;
import com.ironhack.BankSystem.repository.user.RoleRepository;
import com.ironhack.BankSystem.repository.user.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CreditCardService {
    @Autowired
    AccountHolderRepository accountHolderRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    AccountRepository accountRepository;

    private static final Logger LOGGER = LogManager.getLogger(CreditCard.class);

    @Secured({"ROLE_ADMIN"})
    public AccountMV createCreditCard(AccountDto accountDto, int primaryOwnerId, int secondaryOwnerId){
        LOGGER.info("Attempting to create Credit Card Account");
        if (accountDto.getMinimumBalance() != null ||
                accountDto.getSecretKey() != null
        ){
            LOGGER.info("Wrong Attempt to create a Checking Account. Extra input data not allowed");
            throw new ExtraDataNotAllowed("You shouldn't provide a Secret Key or Minimum Balance to create Credit Card Accounts");
        }

        if (accountDto.getInterestRate() == null){
            accountDto.setInterestRate(new BigDecimal("0.2"));
        }else if(accountDto.getInterestRate().compareTo(new BigDecimal("0.2")) > 0 || accountDto.getInterestRate().compareTo(new BigDecimal("0.1")) < 0){
            LOGGER.info("Failed creation of Credit Card Account: Wrong value of Interest Rate: " + accountDto.getInterestRate());
            throw new WrongDataInputException("Interest Rate in Credit Card Accounts can't be greater than 0.2 or lower than 0.1 at creation");
        }

        if (accountDto.getCreditLimit() == null){
            accountDto.setCreditLimit(new BigDecimal("100"));
        }else if(accountDto.getCreditLimit().compareTo(new BigDecimal("100")) < 0 || accountDto.getCreditLimit().compareTo(new BigDecimal("100000")) > 0){
            LOGGER.info("Failed creation of Credit Card Account: Wrong value of Credit Limit: " + accountDto.getCreditLimit());
            throw new WrongDataInputException("Credit Limit in Credit Card Accounts can't be lower than 100 or greater than 100,000 at creation");
        }

        if (primaryOwnerId != 0){
            accountDto.setPrimaryOwner(accountHolderRepository.findById(primaryOwnerId).get());
        }else{
            AccountHolder primaryOwner = accountHolderRepository.save(accountDto.getPrimaryOwner());
            LOGGER.info("New Account Holder created with id " + primaryOwner.getId());
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            User user = new User();
            user.setUsername(accountDto.getUsername());
            user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
            user.setAccountHolder(primaryOwner);
            user = userRepository.save(user);
            LOGGER.info("New User created with id " + user.getId() + " and username " + user.getUsername());
            Role role = new Role("ROLE_HOLDER", user);
            roleRepository.save(role);
        }

        if (secondaryOwnerId != 0){
            accountDto.setSecondaryOwner(accountHolderRepository.findById(secondaryOwnerId).get());
        }else if (accountDto.getSecondaryOwner() != null){
            AccountHolder secondaryOwner = accountHolderRepository.save(accountDto.getSecondaryOwner());
            LOGGER.info("New Account Holder created with id " + secondaryOwner.getId());
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            User user = new User();
            user.setUsername(accountDto.getSecondaryUsername());
            user.setPassword(passwordEncoder.encode(accountDto.getSecondaryPassword()));
            user.setAccountHolder(secondaryOwner);
            user = userRepository.save(user);
            LOGGER.info("New User created with id " + user.getId() + " and username " + user.getUsername());
            Role role = new Role("ROLE_HOLDER", user);
            roleRepository.save(role);
        }

        LOGGER.info("Saving new Checking Account");
        CreditCard account = accountRepository.save(new CreditCard(
                new Money(accountDto.getBalance()),
                accountDto.getPrimaryOwner(),
                accountDto.getSecondaryOwner(),
                accountDto.getCreditLimit(),
                accountDto.getInterestRate()));

        if (accountDto.getSecondaryOwner()!=null){
            return new AccountMV(account.getId(), account.getType(), account.getStatus(), account.getBalance().getAmount(), account.getPrimaryOwner().getName(), account.getSecondaryOwner().getName());
        }else{
            return new AccountMV(account.getId(), account.getType(), account.getStatus(), account.getBalance().getAmount(), account.getPrimaryOwner().getName(), null);
        }
    }
}
