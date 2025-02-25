package com.rewardservice.service.transactioneserviceimpl;

import com.rewardservice.dtos.CustomerData;
import com.rewardservice.entity.Transaction;
import com.rewardservice.globalexception.RewardServiceException;
import com.rewardservice.repository.TransactionRepository;
import com.rewardservice.service.transactioneservice.RewardsService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
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
    public ResponseEntity<List<CustomerData>> getRewardsPointsForTheMonths(Long customerId, Integer months) {
        if (months == null) {
            months = 3;
        }
        LocalDate[] dateRange = calculateDateRange(months);
        LocalDate startDate = dateRange[0];
        LocalDate endDate = dateRange[1];
        List<Transaction> transactions = fetchTransactions(customerId,startDate, endDate);
        return calculateRewardsPoints(transactions,months);
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

    private ResponseEntity<List<CustomerData>> calculateRewardsPoints(List<Transaction> transactions, Integer months) {
        List<CustomerData> customerDetails = transactions.stream().map(transaction -> {
            int calculatedRewardsPoints = calculatePoints(transaction.getAmount());
            CustomerData  customerData = new CustomerData();
            customerData.setCustomerId(transaction.getCustomerId());
            customerData.setTotalRewardsPoints(calculatedRewardsPoints);
            customerData.setMonthName(transaction.getTransactionDate().getMonth().toString());
            customerData.setMonthDuration(months);
            return customerData;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(customerDetails);
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
