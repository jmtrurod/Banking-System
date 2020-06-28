package com.ironhack.BankSystem.repository.user;

import com.ironhack.BankSystem.model.account.StudentChecking;
import com.ironhack.BankSystem.model.user.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountHolderRepository extends JpaRepository<AccountHolder, Integer> {
}
