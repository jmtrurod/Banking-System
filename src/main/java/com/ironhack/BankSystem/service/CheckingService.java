package com.ironhack.BankSystem.service;

import com.ironhack.BankSystem.exception.ExtraDataNotAllowed;
import com.ironhack.BankSystem.exception.MissingDataInput;
import com.ironhack.BankSystem.exception.WrongDataInputException;
import com.ironhack.BankSystem.model.account.Account;
import com.ironhack.BankSystem.model.account.Checking;
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

import java.time.LocalDate;
import java.time.Period;

@Service
public class CheckingService {
    @Autowired
    AccountHolderRepository accountHolderRepository;
    @Autowired
    StudentCheckingService studentCheckingService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    AccountRepository accountRepository;

    private static final Logger LOGGER = LogManager.getLogger(CheckingService.class);

    @Secured({"ROLE_ADMIN"})
    public AccountMV createChecking(AccountDto accountDto, int primaryOwnerId, int secondaryOwnerId){
        LOGGER.info("Attempting to create Checking Account");
        if (accountDto.getSecretKey() == null){
            LOGGER.info("Wrong Attempt to create a Checking Account. Missing input data");
            throw new MissingDataInput("You must provide a Balance, Secret Key and Primary Owner to create a Saving Account");
        }

        if (accountDto.getCreditLimit() != null ||
                accountDto.getInterestRate() != null  ||
                accountDto.getMinimumBalance() != null
        ){
            LOGGER.info("Wrong Attempt to create a Checking Account. Extra input data not allowed");
            throw new ExtraDataNotAllowed("You shouldn't provide a Minimum Balance, Credit Limit or Interest Rate to create Checking Accounts");
        }

        if (primaryOwnerId != 0){
            if (Period.between(accountHolderRepository.findById(primaryOwnerId).get().getDateOfBirth(), LocalDate.now()).getYears() < 24 ){
                LOGGER.info("Redirection from Checking Account creation to Student Checking Account cretion due to Primary Owner age is lower than 24");
                return studentCheckingService.createStudentChecking(accountDto, primaryOwnerId, secondaryOwnerId);
            }
            accountDto.setPrimaryOwner(accountHolderRepository.findById(primaryOwnerId).get());
        }else{
            if (Period.between(accountDto.getPrimaryOwner().getDateOfBirth(), LocalDate.now()).getYears() < 24 ){
                LOGGER.info("Redirection from Checking Account creation to Student Checking Account cretion due to Primary Owner age is lower than 24");
                return studentCheckingService.createStudentChecking(accountDto, primaryOwnerId, secondaryOwnerId);
            }
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
        Checking account = accountRepository.save(new Checking(
                new Money(accountDto.getBalance()),
                accountDto.getSecretKey(),
                accountDto.getPrimaryOwner(),
                accountDto.getSecondaryOwner()));

        if (accountDto.getSecondaryOwner()!=null){
            return new AccountMV(account.getId(), account.getType(), account.getStatus(), account.getBalance().getAmount(), account.getPrimaryOwner().getName(), account.getSecondaryOwner().getName());
        }else{
            return new AccountMV(account.getId(), account.getType(), account.getStatus(), account.getBalance().getAmount(), account.getPrimaryOwner().getName(), null);
        }

    }

}
