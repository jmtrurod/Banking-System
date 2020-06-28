package com.ironhack.BankSystem.model.misc;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class Address {
    @NotNull(message = "Address must have city")
    private String city;
    @NotNull(message = "Address must have country")
    private String country;
    @NotNull
    private String street;
    @NotNull
    private int houseNumber;
    @NotNull
    private String zipCode;

    public Address(){}

    public Address(@NotNull String city, @NotNull String country, @NotNull String street, @NotNull int houseNumber, @NotNull String zipCode) {
        this.city = city;
        this.country = country;
        this.street = street;
        this.houseNumber = houseNumber;
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
