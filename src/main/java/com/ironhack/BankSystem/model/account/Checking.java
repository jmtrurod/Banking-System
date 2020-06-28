package com.ironhack.BankSystem.model.account;

import com.ironhack.BankSystem.enums.Status;
import com.ironhack.BankSystem.enums.Type;
import com.ironhack.BankSystem.model.misc.Money;
import com.ironhack.BankSystem.model.user.AccountHolder;
import org.hibernate.type.SetType;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
public class Checking extends Account{
    private BigDecimal minimumBalance;
    private BigDecimal monthlyMaintenanceFee;
    private boolean penaltyFeeApplied;
    private LocalDateTime referenceInstant;

    public Checking(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        super(balance, primaryOwner, secondaryOwner);
        this.secretKey = secretKey;
        this.status = Status.ACTIVE;
        this.minimumBalance = new BigDecimal("250");
        this.monthlyMaintenanceFee = new BigDecimal("12");
        this.penaltyFeeApplied = false;
        this.type = Type.CHECKING;
        this.referenceInstant = LocalDateTime.now();
    }

    public Checking() {}

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

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
        int monthDif = (int) referenceInstant.until(LocalDateTime.now(), ChronoUnit.MONTHS);
        if (referenceInstant.until(LocalDateTime.now(), ChronoUnit.MONTHS)>0 && this.balance.getAmount().compareTo(new BigDecimal(0))>=0){
            balance.decreaseAmount(
                    monthlyMaintenanceFee.multiply(new BigDecimal(monthDif))
            );
            referenceInstant = referenceInstant.plusMonths(monthDif);
        }
        if (this.balance.getAmount().compareTo(this.minimumBalance)<0 && !penaltyFeeApplied){
            setBalance(new Money(getBalance().getAmount().subtract(getPenaltyFee())));
            penaltyFeeApplied = true;
        }else if(this.balance.getAmount().compareTo(this.minimumBalance)>0 && penaltyFeeApplied){
            penaltyFeeApplied = false;
        }

    }
}
