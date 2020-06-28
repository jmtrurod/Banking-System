package com.ironhack.BankSystem.repository;

import com.ironhack.BankSystem.model.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query("SELECT t.quantity FROM Transaction t INNER JOIN t.fromAccount a WHERE  a.id=:id ORDER BY t.instant ASC")
    public List<BigDecimal> quantityTransactionsByAccount(Integer id);

    @Query("SELECT t.instant FROM Transaction t INNER JOIN t.fromAccount a WHERE  a.id=:id ORDER BY t.instant ASC")
    public List<LocalDateTime> instantTransactionsByAccount(Integer id);

    @Query("SELECT t.instant FROM Transaction t INNER JOIN t.fromAccount a WHERE a.id=:id ORDER BY t.instant DESC")
    public List<LocalDateTime> lastSpent(Integer id);
}
