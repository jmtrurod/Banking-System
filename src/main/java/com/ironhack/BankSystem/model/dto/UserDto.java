package com.ironhack.BankSystem.model.dto;

import javax.validation.constraints.NotNull;

public class UserDto {
    @NotNull
    private String userName;
    @NotNull
    private String password;
    private String hashedKey;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHashedKey() {
        return hashedKey;
    }

    public void setHashedKey(String hashedKey) {
        this.hashedKey = hashedKey;
    }
}
