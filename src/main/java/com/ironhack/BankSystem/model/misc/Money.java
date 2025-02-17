package com.ironhack.BankSystem.model.misc;

import javax.persistence.Embeddable;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.util.Currency;

@Embeddable
public class Money implements Transactional {
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;
    private final Currency currency;
    @NotNull
    @Digits(integer=12, fraction=2)
    private BigDecimal amount;

    public Money(@NotNull BigDecimal amount, Currency currency, RoundingMode rounding) {
        this.currency = currency;
        setAmount(amount.setScale(currency.getDefaultFractionDigits(), rounding));
    }

    //public Money(@NotNull BigDecimal amount, Currency currency) {
    //    this(amount, currency, DEFAULT_ROUNDING);
    //}

    public Money(@NotNull BigDecimal amount) {
        this(amount, EUR, DEFAULT_ROUNDING);
    }

    public Money(){
        this.currency = EUR;
    }

    public BigDecimal increaseAmount(Money money) {
        setAmount(this.amount.add(money.amount));
        return this.amount;
    }

    public BigDecimal increaseAmount(BigDecimal addAmount) {
        setAmount(this.amount.add(addAmount));
        return this.amount;
    }
    public BigDecimal decreaseAmount(Money money) {
        setAmount(this.amount.subtract(money.getAmount()));
        return this.amount;
    }
    public BigDecimal decreaseAmount(BigDecimal addAmount) {
        setAmount(this.amount.subtract(addAmount));
        return this.amount;
    }
    public Currency getCurrency() {
        return this.currency;
    }
    public BigDecimal getAmount() {
        return this.amount;
    }
    private void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String toString() {
        return getCurrency().getSymbol() + " " + getAmount();
    }
}


