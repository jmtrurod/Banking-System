package com.ironhack.BankSystem.model.transaction;

import com.ironhack.BankSystem.model.account.Account;
import com.ironhack.BankSystem.model.user.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private Account fromAccount;
    @ManyToOne
    private Account toAccount;
    private String username;
    private BigDecimal quantity;
    private LocalDateTime instant;
    private LocalDate day;

    public Transaction(){}

    public Transaction(Account fromAccount, Account toAccount, String username, BigDecimal quantity, LocalDateTime instant, LocalDate day) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.username = username;
        this.quantity = quantity;
        this.instant = instant;
        this.day = day;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Account getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(Account fromAccount) {
        this.fromAccount = fromAccount;
    }

    public Account getToAccount() {
        return toAccount;
    }

    public void setToAccount(Account toAccount) {
        this.toAccount = toAccount;
    }

    public String getUser() {
        return username;
    }

    public void setUser(String username) {
        this.username = username;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getInstant() {
        return instant;
    }

    public void setInstant(LocalDateTime instant) {
        this.instant = instant;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", fromAccount=" + fromAccount +
                ", toAccount=" + toAccount +
                ", username='" + username + '\'' +
                ", quantity=" + quantity +
                ", instant=" + instant +
                ", day=" + day +
                '}';
    }
}
