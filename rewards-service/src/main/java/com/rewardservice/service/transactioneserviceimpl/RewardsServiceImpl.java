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
     * Calculates and returns the reward points for a given customer over a specified number of months.
     * If the number of months is not provided, it defaults to 3 months.
     *
     * @param customerId the ID of the customer whose reward points are to be calculated; can be null
     * @param months the number of months over which to calculate reward points; defaults to 3 if null
     * @return a map where the key is the customer ID and the value is another map with the month as the key and the reward points as the value
     */
    @Override
    public Map<Long, Map<String, Integer>> getRewardsPointsForTheMonths(Long customerId, Integer months) {
        if (months == null) {
            months = 3;
        }
        LocalDate[] dateRange = calculateDateRange(months);
        LocalDate startDate = dateRange[0];
        LocalDate endDate = dateRange[1];
        List<Transaction> transactions = fetchTransactions(customerId,startDate, endDate);
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
     * Fetches a list of transactions for a given customer within a specified date range.
     * If the customer ID is not provided, it fetches transactions within the date range for all customers.
     *
     * @param customerId the ID of the customer whose transactions are to be fetched; can be null
     * @param startDate the start date of the date range within which transactions are to be fetched
     * @param endDate the end date of the date range within which transactions are to be fetched
     * @return a list of transactions that match the criteria
     * @throws RewardServiceException if no transactions are found within the specified date range
     */
    private List<Transaction> fetchTransactions(Long customerId,LocalDate startDate, LocalDate endDate) {
       List<Transaction> fetchTransactionDetails;
        if(customerId != null ){
            fetchTransactionDetails = transactionRepository.findByCustomerIdAndTransactionDateBetween(customerId,startDate,endDate);
        } else {
            fetchTransactionDetails = transactionRepository.findByTransactionDateBetween(startDate, endDate);
        }
        if (fetchTransactionDetails.isEmpty()) {
            throw new RewardServiceException("No transaction found for the months.");
        }
        return fetchTransactionDetails;
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
