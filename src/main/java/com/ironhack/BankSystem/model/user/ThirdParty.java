package com.ironhack.BankSystem.model.user;

import javax.persistence.Entity;

@Entity
public class ThirdParty extends User{

    public ThirdParty() {
    }

    public ThirdParty(String username, String password, String hashKey) {
        super(username, password);
        this.hashKey = hashKey;
    }

}
