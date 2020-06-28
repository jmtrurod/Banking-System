package com.ironhack.BankSystem.model.dto;

import javax.validation.constraints.NotNull;

public class ThirdPartyDto {
    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String hashKey;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }
}
