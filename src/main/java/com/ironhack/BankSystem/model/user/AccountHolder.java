package com.ironhack.BankSystem.model.user;

import com.ironhack.BankSystem.model.account.Account;
import com.ironhack.BankSystem.model.misc.Address;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class AccountHolder{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    @Pattern(regexp= "^([A-Za-zÁÉÍÓÚñáéíóúÑ]{0}?[A-Za-zÁÉÍÓÚñáéíóúÑ\\']+[\\s])+([A-Za-zÁÉÍÓÚñáéíóúÑ]{0}?[A-Za-zÁÉÍÓÚñáéíóúÑ\\'])+[\\s]?([A-Za-zÁÉÍÓÚñáéíóúÑ]{0}?[A-Za-zÁÉÍÓÚñáéíóúÑ\\'])?$", message = "Name must be composed by firstname and lastname.")
    private String name;
    @NotNull
    private LocalDate dateOfBirth;
    @Valid
    @NotNull
    @Embedded
    private Address address;
    @Valid
    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="city", column = @Column(name = "mailCity")),
            @AttributeOverride(name="country", column = @Column(name = "mailCountry")),
            @AttributeOverride(name="street", column = @Column(name = "mailStreet")),
            @AttributeOverride(name="houseNumber", column = @Column(name = "mailHouseNumber")),
            @AttributeOverride(name="zipCode", column = @Column(name = "mailZipCode"))
    })
    private Address mailingAddress;
    @OneToMany(mappedBy = "primaryOwner", cascade = CascadeType.ALL)
    private List<Account> primaryAccounts;
    @OneToMany(mappedBy = "secondaryOwner", cascade = CascadeType.ALL)
    private List<Account> secondaryAccounts;
    @OneToOne(mappedBy = "accountHolder")
    private User user;

    public AccountHolder(){}

    public AccountHolder(@NotNull String name, @NotNull LocalDate dateOfBirth, @Valid @NotNull Address address, @Valid @NotNull Address mailingAddress) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.mailingAddress = mailingAddress;
        primaryAccounts = new ArrayList<Account>();
        secondaryAccounts = new ArrayList<Account>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(@Valid Address address) {
        this.address = address;
    }

    public Address getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(Address mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    public List<Account> getPrimaryAccounts() {
        return primaryAccounts;
    }

    public void setPrimaryAccounts(List<Account> primaryAccounts) {
        this.primaryAccounts = primaryAccounts;
    }

    public List<Account> getSecondaryAccounts() {
        return secondaryAccounts;
    }

    public void setSecondaryAccounts(List<Account> secondaryAccounts) {
        this.secondaryAccounts = secondaryAccounts;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AccountHolder{" +
                " name='" + name + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", address=" + address +
                ", mailingAddress=" + mailingAddress +
                ", primaryAccounts=" + primaryAccounts +
                ", secondaryAccounts=" + secondaryAccounts +
                '}';
    }
}
