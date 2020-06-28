package com.ironhack.BankSystem.exception;

public class UnauthorizedUser extends RuntimeException{
    public UnauthorizedUser(String message) {
        super(message);
    }
}