package com.ironhack.BankSystem.model.account;

import com.ironhack.BankSystem.enums.Type;
import com.ironhack.BankSystem.exception.WrongDataInputException;
import com.ironhack.BankSystem.model.misc.Money;
import com.ironhack.BankSystem.model.user.AccountHolder;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
public class CreditCard extends Account{

    private BigDecimal creditLimit;
    private BigDecimal interestRate;
    private LocalDateTime referenceInstant;

    public CreditCard(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, BigDecimal creditLimit, BigDecimal interestRate) {
        super(balance, primaryOwner, secondaryOwner);
        boolean creditLimitComp = creditLimit.compareTo(new BigDecimal("100000")) > 0 || creditLimit.compareTo(new BigDecimal("100")) < 0;
        boolean interestRateComp = interestRate.compareTo(new BigDecimal("0.1")) < 0 || interestRate.compareTo(new BigDecimal("0.2")) > 0;
        if (creditLimitComp && interestRateComp){
            throw new WrongDataInputException("Credit Limit must be between 100,000 and 100 AND Interest Rate must be between 0.2 and 0.1 CreditCard Accounts");
        }
        if (creditLimitComp){
            throw new WrongDataInputException("Credit Limit in Credit Card must be between 100,000 and 100");
        }
        if (interestRateComp){
            throw new WrongDataInputException("Interest Rate in Credit Card must be between 0.2 and 0.1");
        }
        this.creditLimit = creditLimit;
        this.interestRate = interestRate;
        this.referenceInstant = LocalDateTime.now();
        this.type = Type.CREDITCARD;
    }

    public CreditCard() {}

    public void setReferenceInstant(LocalDateTime referenceInstant){
        this.referenceInstant = referenceInstant;
    }

    public LocalDateTime getReferenceInstant(){
        return referenceInstant;
    }

    @Override
    public void update(){
        if(maxTransactionControl.getPendingToUpdate() && LocalDateTime.now().until(maxTransactionControl.getTimeToUpdate(), ChronoUnit.SECONDS)<0){
            maxTransactionControl.setValue(maxTransactionControl.getValueToUpdate());
            maxTransactionControl.setPendingToUpdate(false);
        }
        int numMonthsPassed = (int) referenceInstant.until(LocalDateTime.now(), ChronoUnit.MONTHS);
        if (numMonthsPassed>0 && this.balance.getAmount().compareTo(new BigDecimal(0))>=0){
            BigDecimal multiplier = (interestRate.divide(new BigDecimal("12"), 12, RoundingMode.HALF_EVEN).add(new BigDecimal("1"))).pow(numMonthsPassed);
            setBalance(new Money(getBalance().getAmount().multiply(multiplier)));
            referenceInstant = referenceInstant.plusYears(Math.floorDiv(numMonthsPassed, 12));
            referenceInstant = referenceInstant.plusMonths(numMonthsPassed%12);
        }
    }
}
