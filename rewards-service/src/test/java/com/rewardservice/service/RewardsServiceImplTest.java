package com.rewardservice.service;

import com.rewardservice.dtos.CustomerData;
import com.rewardservice.dtos.TransactionDTO;
import com.rewardservice.entity.Transaction;
import com.rewardservice.globalexception.RewardServiceException;
import com.rewardservice.repository.TransactionRepository;
import com.rewardservice.service.transactioneserviceimpl.RewardsServiceImpl;
import com.rewardservice.utility.TransactionTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for RewardsService.
 */
@SpringBootTest
class RewardsServiceImplTest {
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private RewardsServiceImpl rewardsService;

    private LocalDate today;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rewardsService = new RewardsServiceImpl(transactionRepository);
        today = LocalDate.now();
        startDate = today.minusMonths(3).withDayOfMonth(1);
        endDate = today.minusMonths(1).withDayOfMonth(today.minusMonths(1).lengthOfMonth());

    }

    @Test
    void contextLoads() {
    }

    @Test
    void testValidRewardCalculationFor3Months() {
        var transactions = List.of(new Transaction(1L, 101L, 120.0, today.minusMonths(1)),
                new Transaction(2L, 101L, 75.0, today.minusMonths(2)),
                new Transaction(3L, 102L, 200.0, today.minusMonths(3))
        );
        when(transactionRepository.findByTransactionDateBetween(startDate, endDate)).thenReturn(transactions);
        var rewards = rewardsService.getRewardsPointsForTheMonths(null,3);

        assertEquals(rewards.getStatusCode(), HttpStatus.OK);
        assertNotNull(rewards);
        assertFalse(rewards.getBody().isEmpty());
        assertEquals(true, rewards.getBody().stream().anyMatch(c -> c.getCustomerId() == 101 && c.getTotalRewardsPoints() == 90));
        assertEquals(true, rewards.getBody().stream().anyMatch(c -> c.getCustomerId() == 101 && c.getTotalRewardsPoints() == 25));
        assertEquals(true, rewards.getBody().stream().anyMatch(c -> c.getCustomerId() == 102 && c.getTotalRewardsPoints() == 250));
    }

    @Test
    void testValidRewardCalculationFor3MonthsWithCustomerID() {
        var transactions = List.of(new Transaction(1L, 101L, 120.0, today.minusMonths(1)),
                new Transaction(2L, 101L, 75.0, today.minusMonths(2)),
                new Transaction(3L, 102L, 200.0, today.minusMonths(3))
        );
        when(transactionRepository.findByCustomerIdAndTransactionDateBetween(101L, startDate, endDate)).thenReturn(transactions);
        var rewards = rewardsService.getRewardsPointsForTheMonths(101L,3);

        assertEquals(rewards.getStatusCode(), HttpStatus.OK);
        assertNotNull(rewards);
        assertFalse(rewards.getBody().isEmpty());
        assertEquals(90, rewards.getBody().stream().mapToInt(CustomerData::getTotalRewardsPoints).findFirst().getAsInt());
    }

    @Test
    void testNoTransactionsFound() {
        when(transactionRepository.findByTransactionDateBetween(startDate, endDate)).thenReturn(List.of());

        Exception exception = assertThrows(RewardServiceException.class, () -> {
            rewardsService.getRewardsPointsForTheMonths(null,3);
        });
        String msg = "No transaction found for the months.";
        assertEquals(msg, exception.getMessage());
    }

    @Test
    void testNegativeTransactionAmount() {
        var transactions = List.of(new Transaction(1L, 101L, -50.0, today.minusMonths(1)));

        when(transactionRepository.findByTransactionDateBetween(startDate, endDate)).thenReturn(transactions);

        Exception exception = assertThrows(RewardServiceException.class, () -> {
            rewardsService.getRewardsPointsForTheMonths(null,3);
        });

        assertEquals("Transaction amount cannot be negative.", exception.getMessage());
    }









}
