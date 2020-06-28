package com.ironhack.BankSystem.model.account;

import com.ironhack.BankSystem.enums.Status;
import com.ironhack.BankSystem.enums.Type;
import com.ironhack.BankSystem.exception.WrongDataInputException;
import com.ironhack.BankSystem.model.misc.Money;
import com.ironhack.BankSystem.model.user.AccountHolder;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

@Entity
public class Saving extends Account{
    private BigDecimal minimumBalance;
    private BigDecimal interestRate;
    private Status status;
    private boolean penaltyFeeApplied;
    private LocalDateTime referenceInstant;

    public Saving(){}

    public Saving(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner, BigDecimal minimumBalance, BigDecimal interestRate) {
        super(balance, primaryOwner, secondaryOwner);
        this.secretKey = secretKey;
        this.status = Status.ACTIVE;
        boolean minimumBalanceComp = minimumBalance.compareTo(new BigDecimal("100")) < 0 || minimumBalance.compareTo(new BigDecimal("1000")) > 0;
        boolean interestRateComp = interestRate.compareTo(new BigDecimal("0.5")) > 0 || interestRate.compareTo(new BigDecimal("0")) < 0;
        if (minimumBalanceComp && interestRateComp){
            throw new WrongDataInputException("Minimum Balance must be between 1,000 and 100 AND Interest Rate must be between 0.5 and 0 in Savings Accounts when instantiating");
        }
        if (minimumBalanceComp){
            throw new WrongDataInputException("Minimum Balance in Savings Accounts must be between 1,000 and 100");
        }
        if (interestRateComp){
            throw new WrongDataInputException("Interest Rate in Savings Accounts must be between 0.5 and 0");
        }
        this.minimumBalance = minimumBalance;
        this.interestRate = interestRate;
        this.penaltyFeeApplied = false;
        referenceInstant = LocalDateTime.now();
        this.type = Type.SAVING;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getReferenceInstant() {
        return referenceInstant;
    }

    public void setReferenceInstant(LocalDateTime referenceInstant) {
        this.referenceInstant = referenceInstant;
    }

    @Override
    public void update(){
        if(maxTransactionControl.getPendingToUpdate() && LocalDateTime.now().until(maxTransactionControl.getTimeToUpdate(), ChronoUnit.SECONDS)<0){
            maxTransactionControl.setValue(maxTransactionControl.getValueToUpdate());
            maxTransactionControl.setPendingToUpdate(false);
        }

        if (balance.getAmount().compareTo(minimumBalance)<0 && !penaltyFeeApplied){
            super.balance = new Money(balance.getAmount().subtract(getPenaltyFee()));
            penaltyFeeApplied = true;
        }else if (balance.getAmount().compareTo(minimumBalance)>0){
            penaltyFeeApplied = false;
        }

        int numYearsPassed = (int) referenceInstant.until(LocalDateTime.now(), ChronoUnit.YEARS);
        if (numYearsPassed>0){
            referenceInstant = referenceInstant.plusYears(numYearsPassed);
            BigDecimal multiplier = (interestRate.add(new BigDecimal(1))).pow(numYearsPassed);
            super.balance = new Money(balance.getAmount().multiply(multiplier));
        }
    }
}
