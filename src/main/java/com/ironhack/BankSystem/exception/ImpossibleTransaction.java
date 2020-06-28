package com.ironhack.BankSystem.exception;

public class ImpossibleTransaction extends RuntimeException{
    public ImpossibleTransaction(String message) {
        super(message);
    }
}