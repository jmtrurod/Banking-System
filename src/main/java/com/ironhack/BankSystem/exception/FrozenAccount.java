package com.ironhack.BankSystem.exception;

public class FrozenAccount extends RuntimeException{
    public FrozenAccount(String message) {
        super(message);
    }
}