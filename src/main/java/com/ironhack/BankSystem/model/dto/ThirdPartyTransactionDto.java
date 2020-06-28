package com.ironhack.BankSystem.model.dto;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ThirdPartyTransactionDto {
    @NotNull
    private BigDecimal amount;
    @NotNull
    private int accountId;
    @NotNull
    private String secretKey;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
