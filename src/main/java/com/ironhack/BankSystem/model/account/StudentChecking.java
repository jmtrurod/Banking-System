package com.ironhack.BankSystem.model.account;

import com.ironhack.BankSystem.enums.Status;
import com.ironhack.BankSystem.enums.Type;
import com.ironhack.BankSystem.model.misc.Money;
import com.ironhack.BankSystem.model.user.AccountHolder;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import java.math.BigDecimal;

@Entity
public class StudentChecking extends Account{

    public StudentChecking(Money balance, String secretKey, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        super(balance, primaryOwner, secondaryOwner);
        this.secretKey = secretKey;

        this.type = Type.STUDENT;
    }

    public StudentChecking() {}

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
