package com.ironhack.BankSystem.model.modelView;

public class UserMV {
    private long id;
    private String username;
    private String password;
    private String hashKey;

    public UserMV(){}

    public UserMV(long id, String username, String password, String hashKey) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.hashKey = hashKey;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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
