package com.ironhack.BankSystem.exception;

public class WrongDataInputException extends RuntimeException{
    public WrongDataInputException(String message) {
        super(message);
    }
}
