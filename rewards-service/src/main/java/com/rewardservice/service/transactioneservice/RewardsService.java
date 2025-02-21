package com.rewardservice.service.transactioneservice;

import com.rewardservice.dtos.TransactionDTO;


import java.util.List;
import java.util.Map;

/**
 * Service interface for managing rewards.
 */
public interface RewardsService {

    /**
     * Retrieves the reward points for each customer for the specified number of months.
     *
     * @param months the number of months to consider
     * @return a map where the key is the customer ID and the value is another map with the month as the key and the total reward points as the value
     */
    Map<Long, Map<String, Integer>> getRewardsPointsForTheMonths(Integer months);

    /**
     * Adds a new transaction.
     *
     * @param transactionDTO the transaction data transfer object containing the transaction details
     * @return the added transaction data transfer object
     */
    TransactionDTO addTransaction(TransactionDTO transactionDTO);

    /**
     * Adds multiple transactions.
     *
     * @param transactionDTOList the list of transaction data transfer objects containing the transaction details
     * @return the list of added transaction data transfer objects
     */
    List<TransactionDTO> addTransactions(List<TransactionDTO> transactionDTOList);
}
