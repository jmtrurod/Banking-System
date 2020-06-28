package com.ironhack.BankSystem.controller.impl;

import com.ironhack.BankSystem.controller.interfaces.GeneralController;
import com.ironhack.BankSystem.model.account.Account;
import com.ironhack.BankSystem.model.dto.AccountDto;
import com.ironhack.BankSystem.model.misc.Money;
import com.ironhack.BankSystem.model.modelView.AccountMV;
import com.ironhack.BankSystem.model.modelView.TransferenceMV;
import com.ironhack.BankSystem.model.transaction.Transaction;
import com.ironhack.BankSystem.model.user.User;
import com.ironhack.BankSystem.service.GeneralService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "General Controller")
@RestController
@RequestMapping("/")
public class GeneralControllerImpl implements GeneralController {
    @Autowired
    GeneralService generalService;

    private static final Logger LOGGER = LogManager.getLogger(GeneralControllerImpl.class);

    @PostMapping("/account")
    @ApiOperation(value = "Create an account",
    notes = "Create an account",
    response = Account.class)
    @ResponseStatus(HttpStatus.CREATED)
    public AccountMV createAccount(@RequestBody @Valid AccountDto accountDto){
        LOGGER.info("Attempting to create Account");
        return generalService.createAccount(accountDto);
    }

    @PostMapping("/transfer/accountFrom/{idFrom}/accountTo/{idTo}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Transference between two accounts",
            notes = "Transference between accounts",
            response = Transaction.class)
    public TransferenceMV transference(@PathVariable(name = "idFrom") Integer idFrom,
                                       @PathVariable(name = "idTo") Integer idTo,
                                       @RequestBody @Valid Money balance,
                                       @AuthenticationPrincipal User user){
        LOGGER.info("Logged User: " + user.getUsername() + "; Attempting to make a transference of " + balance.getAmount() + balance.getCurrency() + " from Account " + idFrom + " to Account " + idTo);
        return generalService.transference(idFrom, idTo, balance, user);
    }

    @PostMapping("/credit/{id}")
    @ApiOperation(value = "Admin credit account",
            notes = "Admin credit account")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void creditAccount(@PathVariable(name = "id") Integer id,
                              @RequestBody @Valid Money balance){
        LOGGER.info("Attempting to credit Account " + id + " with " + balance.getAmount() + balance.getCurrency());
        generalService.creditAccount(id, balance);
    }

    @PostMapping("/debit/{id}")
    @ApiOperation(value = "Admin debit account",
            notes = "Admin debit account")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void debitAccount(@PathVariable(name = "id") Integer id, @RequestBody @Valid Money balance){
        LOGGER.info("Attempting to debit Account " + id + " with " + balance.getAmount() + balance.getCurrency());
        generalService.debitAccount(id, balance);
    }

    @GetMapping("/account/{id}")
    @ApiOperation(value = "Account Holder get Account Data",
            notes = "Account Holder get Account Data")
    @ResponseStatus(HttpStatus.OK)
    public AccountMV getAccount(@PathVariable(name = "id") Integer id,
                           @AuthenticationPrincipal User user){
        LOGGER.info("Attempting to get Account " + id + " data by Account Holder with username " + user.getUsername());
        return generalService.getAccount(id, user);
    }

    @PatchMapping("/account/unfreeze/{id}")
    @ApiOperation(value = "Admin unfreeze account",
            notes = "Admin unfreeze account")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void accountUnfreeze(@PathVariable(name = "id") Integer id){
        LOGGER.info("Attempting to Unfreeze Account " + id);
        generalService.accountUnfreeze(id);
    }
}
