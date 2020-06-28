package com.ironhack.BankSystem.model.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.BankSystem.enums.Status;
import com.ironhack.BankSystem.enums.Type;
import com.ironhack.BankSystem.exception.WrongDataInputException;
import com.ironhack.BankSystem.model.misc.MaxTransactionControl;
import com.ironhack.BankSystem.model.misc.Money;
import com.ironhack.BankSystem.model.transaction.Transaction;
import com.ironhack.BankSystem.model.user.AccountHolder;
import com.ironhack.BankSystem.service.GeneralService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    protected Type type;
    protected Status status;
    @Embedded
    protected Money balance;
    @ManyToOne
    @JsonIgnore
    protected AccountHolder primaryOwner;
    @ManyToOne
    @JsonIgnore
    protected AccountHolder secondaryOwner;
    protected BigDecimal penaltyFee;
    @OneToMany(mappedBy = "fromAccount")
    @JsonIgnore
    protected List<Transaction> fromTransactions;
    @OneToMany(mappedBy = "toAccount")
    @JsonIgnore
    protected List<Transaction> toTransactions;
    protected String secretKey;
    @Embedded
    protected MaxTransactionControl maxTransactionControl;

    private static final Logger LOGGER = LogManager.getLogger(Account.class);

    public Account(){}

    public Account(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        this.balance = balance;
        this.primaryOwner = primaryOwner;
        this.secondaryOwner = secondaryOwner;
        this.penaltyFee = new BigDecimal("40");
        this.status = Status.ACTIVE;
        maxTransactionControl = new MaxTransactionControl(new BigDecimal(0), false, new BigDecimal(0), LocalDateTime.now());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Money getBalance() {
        return balance;
    }

    public void setBalance(Money balance) {
        this.balance = balance;
    }

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

    public BigDecimal getPenaltyFee() {
        return penaltyFee;
    }

    public void setPenaltyFee(BigDecimal penaltyFee) {
        this.penaltyFee = penaltyFee;
    }

    public Type getType() {
        return type;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public List<Transaction> getFromTransactions() {
        return fromTransactions;
    }

    public void setFromTransactions(List<Transaction> fromTransactions) {
        this.fromTransactions = fromTransactions;
    }

    public List<Transaction> getToTransactions() {
        return toTransactions;
    }

    public void setToTransactions(List<Transaction> toTransactions) {
        this.toTransactions = toTransactions;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public MaxTransactionControl getMaxTransactionControl() {
        return maxTransactionControl;
    }

    public void update(){
        if(maxTransactionControl.getPendingToUpdate() && LocalDateTime.now().until(maxTransactionControl.getTimeToUpdate(), ChronoUnit.SECONDS)<0){
            maxTransactionControl.setValue(maxTransactionControl.getValueToUpdate());
            maxTransactionControl.setPendingToUpdate(false);
        }
    };

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", type=" + type +
                '}';
    }
}