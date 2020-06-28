package com.ironhack.BankSystem.exception;

public class MissingDataInput extends RuntimeException{
    public MissingDataInput(String message) {
        super(message);
    }
}