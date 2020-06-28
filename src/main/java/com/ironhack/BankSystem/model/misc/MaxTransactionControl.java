package com.ironhack.BankSystem.model.misc;

import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Embeddable
public class MaxTransactionControl {
    private BigDecimal value;
    private Boolean pendingToUpdate;
    private BigDecimal ValueToUpdate;
    private LocalDateTime timeToUpdate;

    public MaxTransactionControl() {
    }

    public MaxTransactionControl(BigDecimal value, Boolean pendingToUpdate, BigDecimal valueToUpdate, LocalDateTime timeToUpdate) {
        this.value = value;
        this.pendingToUpdate = pendingToUpdate;
        ValueToUpdate = valueToUpdate;
        this.timeToUpdate = timeToUpdate;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Boolean getPendingToUpdate() {
        return pendingToUpdate;
    }

    public void setPendingToUpdate(Boolean pendingToUpdate) {
        this.pendingToUpdate = pendingToUpdate;
    }

    public BigDecimal getValueToUpdate() {
        return ValueToUpdate;
    }

    public void setValueToUpdate(BigDecimal valueToUpdate) {
        ValueToUpdate = valueToUpdate;
    }

    public LocalDateTime getTimeToUpdate() {
        return timeToUpdate;
    }

    public void setTimeToUpdate(LocalDateTime timeToUpdate) {
        this.timeToUpdate = timeToUpdate;
    }
}
