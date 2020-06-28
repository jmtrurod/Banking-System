package com.ironhack.BankSystem.service;

import com.ironhack.BankSystem.exception.ExtraDataNotAllowed;
import com.ironhack.BankSystem.exception.MissingDataInput;
import com.ironhack.BankSystem.exception.WrongDataInputException;
import com.ironhack.BankSystem.model.account.Account;
import com.ironhack.BankSystem.model.account.Saving;
import com.ironhack.BankSystem.model.dto.AccountDto;
import com.ironhack.BankSystem.model.misc.Money;
import com.ironhack.BankSystem.model.modelView.AccountMV;
import com.ironhack.BankSystem.model.user.AccountHolder;
import com.ironhack.BankSystem.model.user.Role;
import com.ironhack.BankSystem.model.user.User;
import com.ironhack.BankSystem.repository.user.AccountHolderRepository;
import com.ironhack.BankSystem.repository.AccountRepository;
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
public class SavingService {
    @Autowired
    AccountHolderRepository accountHolderRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    AccountRepository accountRepository;

    private static final Logger LOGGER = LogManager.getLogger(Saving.class);

    @Secured({"ROLE_ADMIN"})
    public AccountMV createSaving(AccountDto accountDto, int primaryOwnerId, int secondaryOwnerId){
        LOGGER.info("Attempting to create Saving Account");
        System.out.println("A");
        if (accountDto.getSecretKey() == null){
            LOGGER.info("Wrong Attempt to create a Saving Account. Missing input data");
            throw new MissingDataInput("You must provide a Balance, Secret Key and Primary Owner to create a Saving Account");
        }
        System.out.println("B");
        if (accountDto.getCreditLimit() != null){
            LOGGER.info("Wrong Attempt to create a Saving Account. Extra input data not allowed");
            throw new ExtraDataNotAllowed("You shouldn't provide a Credit Limit to create Saving Accounts");
        }
        System.out.println("C");
        if (accountDto.getInterestRate() == null){
            accountDto.setInterestRate(new BigDecimal("0.0025"));
        }else if(accountDto.getInterestRate().compareTo(new BigDecimal("0.5")) > 0 || accountDto.getInterestRate().compareTo(new BigDecimal("0")) < 0){
            LOGGER.info("Failed creation of Saving Account: Wrong value of Interest Rate: " + accountDto.getInterestRate());
            throw new WrongDataInputException("Interest Rate in Saving Accounts can't be greater than 0.5 at creation or lower than 0");
        }
        System.out.println("D");
        if (accountDto.getMinimumBalance() == null){
            accountDto.setMinimumBalance(new BigDecimal("1000"));
        }else if(accountDto.getMinimumBalance().compareTo(new BigDecimal("100")) < 0 || accountDto.getMinimumBalance().compareTo(new BigDecimal("1000")) > 0){
            LOGGER.info("Failed creation of Saving Account: Wrong value of Minimum Balance: " + accountDto.getMinimumBalance());
            throw new WrongDataInputException("Minimum Balance in Saving Accounts can't be lower than 100 or greater than 1,000 at creation");
        }
        System.out.println("E");
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
        System.out.println("F");
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
        System.out.println("G");
        LOGGER.info("Saving new Saving Account");
        Saving account = accountRepository.save(new Saving(
                new Money(accountDto.getBalance()),
                accountDto.getSecretKey(),
                accountDto.getPrimaryOwner(),
                accountDto.getSecondaryOwner(),
                accountDto.getMinimumBalance(),
                accountDto.getInterestRate()));

        System.out.println(account);
        if (accountDto.getSecondaryOwner()!=null){
            return new AccountMV(account.getId(), account.getType(), account.getStatus(), account.getBalance().getAmount(), account.getPrimaryOwner().getName(), account.getSecondaryOwner().getName());
        }else{
            return new AccountMV(account.getId(), account.getType(), account.getStatus(), account.getBalance().getAmount(), account.getPrimaryOwner().getName(), null);
        }

    }
}
