package com.ironhack.BankSystem.model.dto;

import com.ironhack.BankSystem.enums.Status;
import com.ironhack.BankSystem.enums.Type;
import com.ironhack.BankSystem.model.user.AccountHolder;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AccountDto {
    @Valid
    private AccountHolder primaryOwner;
    @Valid
    private AccountHolder secondaryOwner;
    private BigDecimal minimumBalance;
    private BigDecimal interestRate;
    private String secretKey;
    @NotNull
    private BigDecimal balance;
    private BigDecimal creditLimit;
    private String username;
    private String password;
    private String secondaryUsername;
    private String secondaryPassword;
    private int primaryOwnerId;
    private int secondaryOwnerId;
    @NotNull
    private String type;

    public AccountDto(){}

    public AccountHolder getPrimaryOwner() {
        return primaryOwner;
    }

    public void setPrimaryOwner(AccountHolder primaryOwner) {
        this.primaryOwner = primaryOwner;
    }

    public AccountHolder getSecondaryOwner() {
        return secondaryOwner;
    }

    public void setSecondaryOwner(AccountHolder secondaryOwner) {
        this.secondaryOwner = secondaryOwner;
    }

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(BigDecimal minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecondaryUsername() {
        return secondaryUsername;
    }

    public void setSecondaryUsername(String secondaryUsername) {
        this.secondaryUsername = secondaryUsername;
    }

    public String getSecondaryPassword() {
        return secondaryPassword;
    }

    public void setSecondaryPassword(String secondaryPassword) {
        this.secondaryPassword = secondaryPassword;
    }

    public int getPrimaryOwnerId() {
        return primaryOwnerId;
    }

    public void setPrimaryOwnerId(int primaryOwnerId) {
        this.primaryOwnerId = primaryOwnerId;
    }

    public int getSecondaryOwnerId() {
        return secondaryOwnerId;
    }

    public void setSecondaryOwnerId(int secondaryOwnerId) {
        this.secondaryOwnerId = secondaryOwnerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "AccountDto{" +
                "primaryOwner=" + primaryOwner +
                ", secondaryOwner=" + secondaryOwner +
                ", minimumBalance=" + minimumBalance +
                ", interestRate=" + interestRate +
                ", secretKey='" + secretKey + '\'' +
                ", balance=" + balance +
                ", creditLimit=" + creditLimit +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", secondaryUsername='" + secondaryUsername + '\'' +
                ", secondaryPassword='" + secondaryPassword + '\'' +
                '}';
    }
}
