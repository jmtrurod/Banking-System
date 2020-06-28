package com.ironhack.BankSystem.controller.interfaces;

import com.ironhack.BankSystem.model.dto.AccountDto;
import com.ironhack.BankSystem.model.misc.Money;
import com.ironhack.BankSystem.model.modelView.AccountMV;
import com.ironhack.BankSystem.model.modelView.TransferenceMV;
import com.ironhack.BankSystem.model.user.User;

public interface GeneralController {
    public AccountMV createAccount(AccountDto accountDto);
    public TransferenceMV transference(Integer idFrom, Integer idTo, Money balance, User user);
    public void creditAccount(Integer id, Money balance);
    public void debitAccount(Integer id, Money balance);
    public AccountMV getAccount(Integer id, User user);

    public void accountUnfreeze(Integer id);
}

