package com.rewardservice.service.transactioneserviceimpl;

import com.rewardservice.dtos.TransactionDTO;
import com.rewardservice.entity.Transaction;
import com.rewardservice.globalexception.RewardServiceException;
import com.rewardservice.repository.TransactionRepository;
import com.rewardservice.service.transactioneservice.RewardsService;
import com.rewardservice.utility.TransactionTransformer;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class to handle business logic related to transactions.
 */
@Service
//@RequiredArgsConstructor
public class RewardsServiceImpl implements RewardsService {

    private final TransactionRepository transactionRepository;


    public RewardsServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Retrieves the reward points for each customer for the specified number of months.
     *
     * @param months the number of months to consider; if null, defaults to 3 months
     * @return a map where the key is the customer ID and the value is another map with the month as the key and the total reward points as the value
     */
    @Override
    public Map<Long, Map<String, Integer>> getRewardsPointsForTheMonths(Integer months) {
        if (months == null) {
            months = 3;
        }
        LocalDate[] dateRange = calculateDateRange(months);
        LocalDate startDate = dateRange[0];
        LocalDate endDate = dateRange[1];
        List<Transaction> transactions = fetchTransactions(startDate, endDate);
        return calculateRewardsPoints(transactions);
    }

    /**
     * Add single transaction to the database
     *
     * @param transactionDTO The transaction data transfer object (DTO) containing transaction details.
     * @return The saved transaction DTO with an assigned ID.
     */
    @Override
    public TransactionDTO addTransaction(TransactionDTO transactionDTO) {
        if (transactionDTO == null) {
            throw new RewardServiceException("TransactionDTO cannot be null");
        }
        Transaction newTransaction = TransactionTransformer.toEntity(transactionDTO);
        newTransaction.setId(null);
        Transaction savedTransaction = transactionRepository.save(newTransaction);
        return TransactionTransformer.toDTO(savedTransaction);
    }

    /**
     * Adds multiple transactions to the database.
     *
     * @param transactionDTOList A list of transaction DTOs to be added.
     * @return A list of saved transaction DTOs with assigned IDs
     */
    @Override
    public List<TransactionDTO> addTransactions(List<TransactionDTO> transactionDTOList) {
        if (transactionDTOList.isEmpty()) {
            throw new RewardServiceException("TransactionDTO list cannot be empty");
        }
        List<Transaction> newTransactions = transactionDTOList
                .stream()
                .map(transactionDTO -> {
                    Transaction transaction = TransactionTransformer.toEntity(transactionDTO);
                    transaction.setId(null);
                    return transaction;
                })
                .collect(Collectors.toList());
        List<Transaction> savedTransactions = transactionRepository.saveAll(newTransactions);
        return savedTransactions.stream()
                .map(TransactionTransformer::toDTO)
                .collect(Collectors.toList());

    }

    /**
     * calculating the range of date.
     *
     * @param months It is the number of months that need to be counted for the date range.
     * @return It will return array for the date range
     */
    private LocalDate[] calculateDateRange(Integer months) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusMonths(months).withDayOfMonth(1);
        LocalDate endDate = today.minusMonths(1).withDayOfMonth(today.minusMonths(1).lengthOfMonth());
        System.out.print("Fetching transaction from " + startDate + " to " + endDate);
        return new LocalDate[]{startDate, endDate};
    }

    /**
     * Fetches the transactions between two dates.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return a list of transactions between the specified dates
     * @throws IllegalArgumentException if no transactions are found for the specified date range
     */
    private List<Transaction> fetchTransactions(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionRepository.findByTransactionDateBetween(startDate, endDate);
        if (transactions.isEmpty()) {
            throw new IllegalArgumentException("No transaction found for the months.");
        }
        return transactions;
    }

    /**
     * Calculates the reward points for each customer based on their transactions.
     *
     * @param transactions the list of transactions
     * @return a map where the key is the customer ID and the value is another map with the month as the key and the total reward points as the value
     */
    private Map<Long, Map<String, Integer>> calculateRewardsPoints(List<Transaction> transactions) {
        return transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getCustomerId,
                        Collectors.groupingBy(txn -> txn.getTransactionDate().getMonth().toString(),
                                Collectors.summingInt(txn -> calculatePoints(txn.getAmount())))));
    }

    /**
     * Calculates the reward points based on the transaction amount.
     *
     * @param amount the transaction amount
     * @return the calculated reward points
     * @throws IllegalArgumentException if the transaction amount is negative
     */
    private int calculatePoints(Double amount) {
        if (amount == null || amount < 0) {
            throw new RewardServiceException("Transaction amount cannot be negative.");
        }
        int points = 0;
        if (amount > 100) {
            points += (int) ((amount - 100) * 2);
            amount = 100.0;
        }
        if (amount > 50) {
            points += (int) (amount - 50);
        }
        return points;
    }

}
