package com.rewardservice.service.transactioneservice;

import com.rewardservice.dtos.CustomerData;
import com.rewardservice.dtos.TransactionDTO;
import org.springframework.http.ResponseEntity;


import java.util.List;

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
    ResponseEntity<List<CustomerData>> getRewardsPointsForTheMonths(Long customerId, Integer months);

}
