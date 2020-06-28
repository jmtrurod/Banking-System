package com.ironhack.BankSystem.service;

import com.ironhack.BankSystem.exception.WrongDataInputException;
import com.ironhack.BankSystem.model.account.Account;
import com.ironhack.BankSystem.model.dto.ThirdPartyDto;
import com.ironhack.BankSystem.model.dto.ThirdPartyTransactionDto;
import com.ironhack.BankSystem.model.modelView.UserMV;
import com.ironhack.BankSystem.model.user.Role;
import com.ironhack.BankSystem.model.user.ThirdParty;
import com.ironhack.BankSystem.model.user.User;
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
public class ThirdPartyService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    AccountRepository accountRepository;

    private static final Logger LOGGER = LogManager.getLogger(ThirdPartyService.class);

    @Secured({"ROLE_ADMIN"})
    public UserMV createThirdParty(ThirdPartyDto thirdPartyDto){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (userRepository.findByUsername(thirdPartyDto.getUsername())!=null){
            LOGGER.info("Attempt to create Third Party with username: " + thirdPartyDto.getUsername() + " already existing");
            throw new WrongDataInputException("User with username " + thirdPartyDto.getUsername() + " already exists");
        }
        User user = userRepository.save(new ThirdParty(thirdPartyDto.getUsername(), passwordEncoder.encode(thirdPartyDto.getPassword()), thirdPartyDto.getHashKey()));
        Role role = new Role("ROLE_THIRDPARTY", user);
        roleRepository.save(role);
        LOGGER.info("Succesfull creation of Third Party with username: " + thirdPartyDto.getUsername());
        return new UserMV(user.getId(), user.getUsername(), user.getPassword(), user.getHashKey());
    }

    @Secured({"ROLE_THIRDPARTY"})
    public void creditThirdParty(String hashkey, ThirdPartyTransactionDto thirdPartyTransactionDto, User user){
        LOGGER.info("User " + user.getUsername() + " attempt to credit Account with id " + thirdPartyTransactionDto.getAccountId());;
        if (!accountRepository.findById(thirdPartyTransactionDto.getAccountId()).isPresent()){
            LOGGER.info("Failed credit - Account with id " + thirdPartyTransactionDto.getAccountId() + " doesn't exist");
            throw new WrongDataInputException("There is no account with id " + thirdPartyTransactionDto.getAccountId());
        }
        Account account = accountRepository.findById(thirdPartyTransactionDto.getAccountId()).get();
        if (account.getSecretKey()==null){
            LOGGER.info("Failed credit - Account with id " + thirdPartyTransactionDto.getAccountId() + " can't be credited by Third Parties because it's a Credit Card Account");
            throw new WrongDataInputException("The Account with Id " + thirdPartyTransactionDto.getAccountId() + " is a " + account.getType() + " and Third Parties can't act on them");
        }
        if (!account.getSecretKey().equals(thirdPartyTransactionDto.getSecretKey())){
            LOGGER.info("Failed credit - Third Party " + user.getUsername() + " didn't introduce the right Secret Key");
            throw new WrongDataInputException("Wrong password");
        }
        user = userRepository.findById(user.getId()).get();
        if (!user.getHashKey().equals(hashkey)){
            LOGGER.info("Failed credit - Third Party " + user.getUsername() + " didn't introduce the right Hash Key");
            throw new WrongDataInputException("Wrong Hash Key");
        }
        if (thirdPartyTransactionDto.getAmount().compareTo(new BigDecimal(0))<0){
            LOGGER.info("Failed credit - Third Party " + user.getUsername() + " introduced negative money");
            throw new WrongDataInputException("Negative balance not allowed in credit");
        }

        account.update();
        account.getBalance().increaseAmount(thirdPartyTransactionDto.getAmount());
        accountRepository.save(account);
        LOGGER.info("Successful credit from Third Party " + user.getUsername() + " to Account with Id " + thirdPartyTransactionDto.getAccountId() + ". Credit amount is " + thirdPartyTransactionDto.getAmount() + "EUR");
    }

    @Secured({"ROLE_THIRDPARTY"})
    public void debitThirdParty(String hashkey, ThirdPartyTransactionDto thirdPartyTransactionDto, User user){
        LOGGER.info("User " + user.getUsername() + " attempt to debit Account with id " + thirdPartyTransactionDto.getAccountId());;
        if (!accountRepository.findById(thirdPartyTransactionDto.getAccountId()).isPresent()){
            LOGGER.info("Failed debit - Account with id " + thirdPartyTransactionDto.getAccountId() + " doesn't exist");
            throw new WrongDataInputException("There is no account with id " + thirdPartyTransactionDto.getAccountId());
        }
        Account account = accountRepository.findById(thirdPartyTransactionDto.getAccountId()).get();
        if (account.getSecretKey()==null){
            LOGGER.info("Failed debit - Account with id " + thirdPartyTransactionDto.getAccountId() + " can't be debited by Third Parties because it's a Credit Card Account");
            throw new WrongDataInputException("The Account with Id " + thirdPartyTransactionDto.getAccountId() + " is a " + account.getType() + " and Third Parties can't act on them");
        }
        if (!account.getSecretKey().equals(thirdPartyTransactionDto.getSecretKey())){
            LOGGER.info("Failed debit - Third Party " + user.getUsername() + " didn't introduce the right Secret Key");
            throw new WrongDataInputException("Wrong password");
        }
        user = userRepository.findById(user.getId()).get();
        if (!user.getHashKey().equals(hashkey)){
            LOGGER.info("Failed debit - Third Party " + user.getUsername() + " didn't introduce the right Hash Key");
            throw new WrongDataInputException("Wrong Hash Key");
        }
        if (thirdPartyTransactionDto.getAmount().compareTo(new BigDecimal(0))<0){
            LOGGER.info("Failed debit - Third Party " + user.getUsername() + " introduced negative money");
            throw new WrongDataInputException("Negative balance not allowed in debit");
        }

        account.update();
        account.getBalance().decreaseAmount(thirdPartyTransactionDto.getAmount());
        accountRepository.save(account);
        LOGGER.info("Successful debit from Third Party " + user.getUsername() + " to Account with Id " + thirdPartyTransactionDto.getAccountId() + ". Debited amount is " + thirdPartyTransactionDto.getAmount() + "EUR");
    }
}
