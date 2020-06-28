package com.ironhack.BankSystem.service;

import com.ironhack.BankSystem.enums.Status;
import com.ironhack.BankSystem.enums.Type;
import com.ironhack.BankSystem.exception.*;
import com.ironhack.BankSystem.model.account.*;
import com.ironhack.BankSystem.model.dto.AccountDto;
import com.ironhack.BankSystem.model.misc.Money;
import com.ironhack.BankSystem.model.modelView.AccountMV;
import com.ironhack.BankSystem.model.modelView.TransferenceMV;
import com.ironhack.BankSystem.model.transaction.Transaction;
import com.ironhack.BankSystem.model.user.AccountHolder;
import com.ironhack.BankSystem.model.user.User;
import com.ironhack.BankSystem.repository.TransactionRepository;
import com.ironhack.BankSystem.repository.AccountRepository;
import com.ironhack.BankSystem.repository.user.AccountHolderRepository;
import com.ironhack.BankSystem.repository.user.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import javax.persistence.GeneratedValue;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GeneralService {
    @Autowired
    CheckingService checkingService;
    @Autowired
    StudentCheckingService studentCheckingService;
    @Autowired
    SavingService savingService;
    @Autowired
    CreditCardService creditCardService;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountHolderRepository accountHolderRepository;

    private static final Logger LOGGER = LogManager.getLogger(GeneralService.class);

    @Secured({"ROLE_ADMIN"})
    @Transactional
    public AccountMV createAccount(AccountDto accountDto){
        String type = accountDto.getType();
        int primaryOwnerId = accountDto.getPrimaryOwnerId();
        int secondaryOwnerId = accountDto.getSecondaryOwnerId();

        if (primaryOwnerId == 0 && accountDto.getPrimaryOwner() == null){
            LOGGER.info("Failed creation of Account: no Primary Owner or Primary Owner id was given");
            throw new MissingDataInput("You must provide a new Primary Owner or a Primary Owner Id to create an account");
        }
        if (primaryOwnerId != 0 && accountDto.getPrimaryOwner() != null){
            LOGGER.info("Failed creation of Account: Primary Owner and Primary Owner id were given at the same time");
            throw new ExtraDataNotAllowed("You shouldn't provide Primary Owner data and a Primary Owner Id");
        }
        if (secondaryOwnerId != 0 && accountDto.getSecondaryOwner() != null){
            LOGGER.info("Failed creation of Account: Secondary Owner and Secondary Owner id were given at the same time");
            throw new ExtraDataNotAllowed("You shouldn't provide Secondary Owner data and a Secondary Owner Id");
        }
        if (primaryOwnerId == 0){
            if (accountDto.getUsername()==null || accountDto.getPassword()==null){
                LOGGER.info("Failed creation of Account: Username and Password weren't provided to create Primary Owner");
                throw new MissingDataInput("You must provide an Username and a Password to create a new Primary Owner");
            }else if(userRepository.findByUsername(accountDto.getUsername()) != null){
                LOGGER.info("Failed creation of Account: Username" + accountDto.getUsername() + " already exists");
                throw new WrongDataInputException("Username " + accountDto.getUsername() + " already exists");
            }
        }

        if (accountDto.getSecondaryOwner()!=null){
            if (accountDto.getSecondaryUsername()==null || accountDto.getSecondaryPassword()==null) {
                LOGGER.info("Failed creation of Account: Username and Password weren't provided to create Secondary Owner");
                throw new MissingDataInput("You must provide an Username and a Password to create a new Secondary Owner");
            }else if(userRepository.findByUsername(accountDto.getSecondaryUsername()) != null){
                LOGGER.info("Failed creation of Account: Username" + accountDto.getSecondaryUsername() + " already exists");
                throw new WrongDataInputException("Username " + accountDto.getSecondaryUsername() + " already exists");
            }else if(accountDto.getPrimaryOwner()!=null && accountDto.getUsername().equals(accountDto.getSecondaryUsername())){
                LOGGER.info("Failed creation of Account: Username must not be the same as Secondary Username");
                throw new WrongDataInputException("Username must not be the same as Secondary Username");
            }
        }

        if (primaryOwnerId!=0 && !accountHolderRepository.findById(primaryOwnerId).isPresent()){
            LOGGER.info("Failed creation of Account: Primary Owner with id " + primaryOwnerId + " doesn't exist");
            throw new WrongDataInputException("There's no Account Holder with id " + primaryOwnerId);
        }
        if (secondaryOwnerId!=0 && !accountHolderRepository.findById(secondaryOwnerId).isPresent()){
            LOGGER.info("Failed creation of Account: Secondary Owner with id " + secondaryOwnerId + " doesn't exist");
            throw new WrongDataInputException("There's no Account Holder with id " + secondaryOwnerId);
        }

        if (primaryOwnerId!=0 && secondaryOwnerId == primaryOwnerId){
            LOGGER.info("Failed creation of Account: Primary Owner Id must not be equal to Secondary Owner Id");
            throw new WrongDataInputException("Primary Owner Id must not be equal to Secondary Owner Id");
        }


        if (accountDto.getBalance().compareTo(new BigDecimal(0))<0){
            LOGGER.info("Attempt to create an Account with negative balance");
            throw new WrongDataInputException("You can't create an Account with negative balance");
        }

        switch (type.toUpperCase()) {
            case "CHECKING":
                LOGGER.info("Attempting to create a Checking Account");
                return checkingService.createChecking(accountDto, primaryOwnerId, secondaryOwnerId);
            case "STUDENT":
                LOGGER.info("Attempting to create a Student Checking Account");
                return studentCheckingService.createStudentChecking(accountDto, primaryOwnerId, secondaryOwnerId);
            case "SAVING":
                LOGGER.info("Attempting to create a Saving Account");
                return savingService.createSaving(accountDto, primaryOwnerId, secondaryOwnerId);
            case "CREDITCARD":
                LOGGER.info("Attempting to create a Credit Card Account");
                return creditCardService.createCreditCard(accountDto, primaryOwnerId, secondaryOwnerId);
            default:
                LOGGER.info("Failed creation of Account: No right Account type was provided");
                throw new WrongDataInputException("You must introduce [CHECKING, STUDENT, SAVING, CREDITCARD] as type to create a new Account");
        }
    }

    @Secured({"ROLE_HOLDER"})
    public TransferenceMV transference(Integer fromId, Integer toId, Money balance, User user){
        if (balance.getAmount().compareTo(new BigDecimal(0))<0){
            LOGGER.info("Failed in transference: Attempting to transfer negative money");
            throw new WrongDataInputException("You can't transfer negative money");
        }
        user = userRepository.findByUsername(user.getUsername());

        Optional<Account> accountFrom = accountRepository.findById(fromId);
        Optional<Account> accountTo = accountRepository.findById(toId);

        if (!accountFrom.isPresent() && !accountTo.isPresent()){
            LOGGER.info("Failed in transference: Account Ids don't exist");
            throw new WrongDataInputException("There is no account with id " + fromId + " or id " + toId);
        }else if (!accountFrom.isPresent()){
            LOGGER.info("Failed in transference: Account Id Transmiter doesn't exist");
            throw new WrongDataInputException("There is no account with id " + fromId);
        }else if (!accountTo.isPresent()){
            LOGGER.info("Failed in transference: Account Id Receiver doesn't exist");
            throw new WrongDataInputException("There is no account with id " + toId);
        }

        if (user.getAccountHolder().getId() != accountFrom.get().getPrimaryOwner().getId()){
            if (accountFrom.get().getSecondaryOwner() == null
                    || user.getAccountHolder().getId() != accountFrom.get().getSecondaryOwner().getId()){
                LOGGER.info("Failed in transference: User " + user.getUsername() + " trying to transfer from Account " + accountFrom + " is not allowed");
                throw new UnauthorizedUser("Account Holder with id " + user.getAccountHolder().getId() + " can't execute transactions from Account " + fromId);
            }
        }

        if (accountFrom.get().getStatus().equals(Status.FROZEN)){
            LOGGER.info("Failed in transference: Attempting to transfer money from a Frozen Account");
            throw new FrozenAccount("This Account is frozen, so no transactions can be achieved");
        }

        LOGGER.info("Updating accounts involved in transference");
        accountFrom.get().update();
        accountRepository.save(accountFrom.get());
        accountTo.get().update();
        accountRepository.save(accountTo.get());
        Money accountBalance = new Money(accountFrom.get().getBalance().getAmount());
        accountBalance.decreaseAmount(balance);
        if (accountBalance.getAmount().compareTo(new BigDecimal(0)) < 0){
            LOGGER.info("Failed in transference: Not enough money to make the transaction");
            throw new ImpossibleTransaction("Account with id " +
                    fromId +
                    " can't execute a transference of " +
                    balance.getAmount() + " " +
                    balance.getCurrency() +
                    " because actual balance is " +
                    accountFrom.get().getBalance().getAmount() +
                    accountFrom.get().getBalance().getCurrency());
        }

        List<LocalDateTime> instantTransactionsByAccount = transactionRepository.instantTransactionsByAccount(fromId);
        BigDecimal last24hSpent = new BigDecimal(0);
        if (instantTransactionsByAccount.size()!=0) {
            List<BigDecimal> quantityTransactionsByAccount = transactionRepository.quantityTransactionsByAccount(fromId);
            LocalDateTime timeToUpdate = null;
            for (int i = 0; i<instantTransactionsByAccount.size(); i++) {
                if (instantTransactionsByAccount.get(i).until(LocalDateTime.now(), ChronoUnit.HOURS) < 24) {
                    if (timeToUpdate==null){
                        timeToUpdate = instantTransactionsByAccount.get(0);
                    }
                    last24hSpent = last24hSpent.add(quantityTransactionsByAccount.get(i));
                }
            }

            last24hSpent = last24hSpent.add(balance.getAmount());
            BigDecimal compareValue = accountFrom.get().getMaxTransactionControl().getValue();

            if (compareValue.compareTo(new BigDecimal("0"))!=0 && compareValue.multiply(new BigDecimal("2.5")).compareTo(last24hSpent) < 0){
                accountFrom.get().setStatus(Status.FROZEN);
                accountRepository.save(accountFrom.get());
                LOGGER.info("Failed in transference: Frozing Account: Attempting to transfer a more than 150% higher amount than any other day");
                throw new FrozenAccount("Failed in transference: Frozing Account: Attempting to transfer a more than 150% higher amount than any other day");
            }

            if (accountFrom.get().getMaxTransactionControl().getValue().compareTo(last24hSpent) < 0){
                accountFrom.get().getMaxTransactionControl().setPendingToUpdate(true);
                accountFrom.get().getMaxTransactionControl().setValueToUpdate(last24hSpent);
                accountFrom.get().getMaxTransactionControl().setTimeToUpdate(timeToUpdate.plusDays(1));
                accountRepository.save(accountFrom.get());
                LOGGER.info("Updating max value spent in 24h for Account " + accountFrom.get().getId());
            }

            List<LocalDateTime> lastSpents = transactionRepository.lastSpent(fromId);
            LocalDateTime lastSpent = null;
            if (lastSpents.size()!=0){
                lastSpent = lastSpents.get(0);
            }

            if (lastSpent != null && lastSpent.until(LocalDateTime.now(), ChronoUnit.SECONDS) < 1) {
                accountFrom.get().setStatus(Status.FROZEN);
                accountRepository.save(accountFrom.get());
                LOGGER.info("Failed in transference: Frozing Account: Attempting to tranfer twice in less than 1 sec");
                throw new FrozenAccount("Tried to do 2 transactions in 1 second period");
            }

            accountFrom.get().getMaxTransactionControl().setTimeToUpdate(timeToUpdate);
        }else{
            accountFrom.get().getMaxTransactionControl().setPendingToUpdate(true);
            accountFrom.get().getMaxTransactionControl().setValueToUpdate(balance.getAmount());
            accountFrom.get().getMaxTransactionControl().setTimeToUpdate(LocalDateTime.now().plusDays(1));
            accountRepository.save(accountFrom.get());
            LOGGER.info("Updating max value spent in 24h for Account " + accountFrom.get().getId());
        }

        Transaction transaction = saveBothAccounts(accountRepository.findById(fromId).get(), accountRepository.findById(toId).get(), balance, user.getUsername());
        System.out.println(transaction);
        return new TransferenceMV(transaction.getFromAccount().getId(), transaction.getToAccount().getId(), transaction.getUser(), transaction.getInstant(), transaction.getQuantity());
    }

    @Secured({"ROLE_HOLDER"})
    @Transactional
    public Transaction saveBothAccounts(Account accountFrom, Account accountTo, Money balance, String username){
        accountFrom.getBalance().decreaseAmount(balance);
        accountTo.getBalance().increaseAmount(balance);

        accountFrom =  accountRepository.save(accountFrom);
        accountTo = accountRepository.save(accountTo);

        Transaction transaction = new Transaction(accountFrom, accountTo, username, balance.getAmount(), LocalDateTime.now(), LocalDate.now());
        LOGGER.info("Saving transaction");
        return transactionRepository.save(transaction);
    }

    @Secured({"ROLE_ADMIN"})
    @Transactional
    public void creditAccount(Integer id, Money balance){
        if (balance.getAmount().compareTo(new BigDecimal(0))<0){
            LOGGER.info("Failed credit: Trying to credit negative money");
            throw new WrongDataInputException("You can't credit negative money");
        }
        Account account = accountRepository.findById(id).orElseThrow(()->new WrongDataInputException("There's no account with id " + id));
        account.update();
        account.getBalance().increaseAmount(balance);
        accountRepository.save(account);
        account.update();
        LOGGER.info("Saved and updated credited account");
    }

    @Secured({"ROLE_ADMIN"})
    @Transactional
    public void debitAccount(Integer id, Money balance){
        if (balance.getAmount().compareTo(new BigDecimal(0))<0){
            LOGGER.info("Failed debit: Trying to debit negative money");
            throw new WrongDataInputException("You can't debit negative money");
        }
        Account account = accountRepository.findById(id).orElseThrow(()->new WrongDataInputException("There's no account with id " + id));
        account.update();
        account.getBalance().decreaseAmount(balance);
        accountRepository.save(account);
        account.update();
        LOGGER.info("Saved and updated debited account");
    }

    @Secured({"ROLE_HOLDER"})
    public AccountMV getAccount(Integer id, User user) {

        user = userRepository.findByUsername(user.getUsername());

        Optional<Account> account = accountRepository.findById(id);

        if (!account.isPresent()){
            LOGGER.info("Failed in transference: Account " + id + " doesn't exist");
            throw new WrongDataInputException("There is no account with id " + id);
        }

        if (user.getAccountHolder().getId() != account.get().getPrimaryOwner().getId()){
            if (account.get().getSecondaryOwner() == null
                    || user.getAccountHolder().getId() != account.get().getSecondaryOwner().getId()){
                LOGGER.info("Failed in transference: User " + user.getUsername() + " trying to get Data from Account " + account + " is not allowed");
                throw new UnauthorizedUser("Account Holder with id " + user.getAccountHolder().getId() + " can't get Data from Account " + id);
            }
        }

        account.get().update();
        accountRepository.save(account.get());

        if (account.get().getSecondaryOwner()!=null){
            return new AccountMV(account.get().getId(), account.get().getType(), account.get().getStatus(), account.get().getBalance().getAmount(), account.get().getPrimaryOwner().getName(), account.get().getSecondaryOwner().getName());
        }else{
            return new AccountMV(account.get().getId(), account.get().getType(), account.get().getStatus(), account.get().getBalance().getAmount(), account.get().getPrimaryOwner().getName(), null);
        }
    }

    @Secured({"ROLE_ADMIN"})
    @Transactional
    public void accountUnfreeze(Integer id) {

        Optional<Account> account = accountRepository.findById(id);
        if (!account.isPresent()){
            LOGGER.info("Account with id " + id + " doesn't exist");
            throw new WrongDataInputException("Account with id " + id + " doesn't exist");
        }
        if (account.get().getStatus().equals(Status.ACTIVE)){
            LOGGER.info("Account with id " + id + " isn't frozen");
            throw new WrongDataInputException("Account with id " + id + " isn't frozen");
        }

        account.get().setStatus(Status.ACTIVE);
        account.get().update();
        accountRepository.save(account.get());

    }

}