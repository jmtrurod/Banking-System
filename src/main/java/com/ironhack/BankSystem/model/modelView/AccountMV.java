package com.ironhack.BankSystem.model.modelView;

import com.ironhack.BankSystem.enums.Status;
import com.ironhack.BankSystem.enums.Type;
import com.ironhack.BankSystem.model.user.AccountHolder;

import java.math.BigDecimal;

public class AccountMV {
    private Integer id;
    private Type type;
    private Status status;
    private BigDecimal balance;
    private String primaryOwnerName;
    private String secondaryOwnerName;

    public AccountMV(){}

    public AccountMV(Integer id, Type type, Status status, BigDecimal balance, String primaryOwnerName, String secondaryOwnerName) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.balance = balance;
        this.primaryOwnerName = primaryOwnerName;
        this.secondaryOwnerName = secondaryOwnerName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getPrimaryOwnerName() {
        return primaryOwnerName;
    }

    public void setPrimaryOwnerName(String primaryOwnerName) {
        this.primaryOwnerName = primaryOwnerName;
    }

    public String getSecondaryOwnerName() {
        return secondaryOwnerName;
    }

    public void setSecondaryOwnerName(String secondaryOwnerName) {
        this.secondaryOwnerName = secondaryOwnerName;
    }
}
