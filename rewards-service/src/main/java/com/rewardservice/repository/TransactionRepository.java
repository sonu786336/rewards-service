package com.rewardservice.repository;

import com.rewardservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for managing transactions.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Finds all transactions that occurred between the specified start and end dates.
     *
     * @param startDate the start date of the period to search for transactions, inclusive
     * @param endDate   the end date of the period to search for transactions, inclusive
     * @return a list of transactions that occurred between the specified dates
     */
    List<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);
}
