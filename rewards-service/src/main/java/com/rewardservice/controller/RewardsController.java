package com.rewardservice.controller;

import com.rewardservice.dtos.CustomerData;
import com.rewardservice.service.transactioneservice.RewardsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for handling transaction-related API requests.
 */
@RestController
//@RequiredArgsConstructor
@RequestMapping("/v1/api/rewards")
@RestControllerAdvice
public class RewardsController {
    private final RewardsService rewardsService;

    public RewardsController(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }

    /**
     * Endpoint to get reward points for a customer over a specified number of months.
     * If the customer ID or the number of months is not provided, it defaults to calculating for all customers over the last 3 months.
     *
     * @param customerId the ID of the customer whose reward points are to be fetched; can be null
     * @param months the number of months over which to calculate reward points; defaults to 3 if null
     * @return a map where the key is the customer ID and the value is another map with the month as the key and the reward points as the value
     */
    @GetMapping()
    public ResponseEntity<List<CustomerData>> getRewardsPointsForTheMonths(@RequestParam(required = false) Long customerId, @RequestParam(required = false) Integer months) {
        return rewardsService.getRewardsPointsForTheMonths(customerId,months);
    }


}
