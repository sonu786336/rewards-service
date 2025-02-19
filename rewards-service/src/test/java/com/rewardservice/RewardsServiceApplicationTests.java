package com.rewardservice;

import com.rewardservice.entity.Transaction;
import com.rewardservice.repository.TransactionRepository;
import com.rewardservice.service.impl.RewardsServiceImpl;
import com.rewardservice.service.inter_face.RewardsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class RewardsServiceApplicationTests {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private RewardsServiceImpl rewardsService;

    private LocalDate today;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rewardsService = new RewardsServiceImpl(transactionRepository, modelMapper);
        today = LocalDate.now();
        startDate = today.minusMonths(3).withDayOfMonth(1);
        endDate = today.minusMonths(1).withDayOfMonth(today.minusMonths(1).lengthOfMonth());
        System.out.println("Test Running - Expected Date Range: " + startDate + " to " + endDate);

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
        var rewards = rewardsService.getRewardsPointsForTheMonths(3);

        assertNotNull(rewards);
        assertFalse(rewards.isEmpty());
        assertEquals(90, rewards.get(101L).get(today.minusMonths(1).getMonth().toString()));
        assertEquals(25, rewards.get(101L).get(today.minusMonths(2).getMonth().toString()));
        assertEquals(250, rewards.get(102L).get(today.minusMonths(3).getMonth().toString()));
    }

    @Test
    void testNoTransactionsFound() {
        when(transactionRepository.findByTransactionDateBetween(startDate, endDate)).thenReturn(List.of());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            rewardsService.getRewardsPointsForTheMonths(3);
        });
        String msg = "No transaction found for the months.";
        assertEquals(msg, exception.getMessage());
    }

    @Test
    void testNegativeTransactionAmount() {
        var transactions = List.of(new Transaction(1L, 101L, -50.0, today.minusMonths(1)));

        when(transactionRepository.findByTransactionDateBetween(startDate, endDate)).thenReturn(transactions);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            rewardsService.getRewardsPointsForTheMonths(3);
        });

        assertEquals("Transaction amount cannot be negative.", exception.getMessage());
    }


}
