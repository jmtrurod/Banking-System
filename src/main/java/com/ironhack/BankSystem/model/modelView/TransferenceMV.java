package com.ironhack.BankSystem.model.modelView;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferenceMV {
    private Integer fromAccountId;
    private Integer toAccountId;
    private String username;
    private LocalDateTime instant;
    private BigDecimal balance;

    public TransferenceMV(){}

    public TransferenceMV(Integer fromAccountId, Integer toAccountId, String username, LocalDateTime instant, BigDecimal balance) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.username = username;
        this.instant = instant;
        this.balance = balance;
    }

    public Integer getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Integer fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Integer getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Integer toAccountId) {
        this.toAccountId = toAccountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getInstant() {
        return instant;
    }

    public void setInstant(LocalDateTime instant) {
        this.instant = instant;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
