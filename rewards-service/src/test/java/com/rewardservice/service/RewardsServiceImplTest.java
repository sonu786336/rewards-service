package com.rewardservice.service;

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

        assertNotNull(rewards);
        assertFalse(rewards.isEmpty());
        assertEquals(90, rewards.get(101L).get(today.minusMonths(1).getMonth().toString()));
        assertEquals(25, rewards.get(101L).get(today.minusMonths(2).getMonth().toString()));
        assertEquals(250, rewards.get(102L).get(today.minusMonths(3).getMonth().toString()));
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

    @Test
    void testAddTransaction_Success() {
        TransactionDTO transactionDTO = new TransactionDTO(101L, 120.0, LocalDate.of(2025, 2, 10));
        Transaction savedTransaction = new Transaction(1L, 101L, 120.0, LocalDate.of(2025, 2, 10));

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        Transaction transformedEntity = TransactionTransformer.toEntity(transactionDTO);
        TransactionDTO transformedDTO = TransactionTransformer.toDTO(savedTransaction);

        assertEquals(transformedEntity.getCustomerId(),transformedDTO.getCustomerId());
        assertEquals(transformedEntity.getAmount(), transformedDTO.getAmount());
        assertEquals(transformedEntity.getTransactionDate(),transformedDTO.getTransactionDate());

        assertEquals(transformedEntity.getCustomerId(),savedTransaction.getCustomerId());
        assertEquals(transformedEntity.getAmount(), savedTransaction.getAmount());
        assertEquals(transformedEntity.getTransactionDate(),savedTransaction.getTransactionDate());
    }

    @Test
    void testAddTransactions_Success() {
        List<TransactionDTO> transactionDTOList = List.of(
                new TransactionDTO(101L, 120.0, LocalDate.of(2025, 2, 10)),
                new TransactionDTO(102L, 75.0, LocalDate.of(2025, 1, 15))
        );

        List<Transaction> savedTransactionList = List.of(
                new Transaction(1L, 101L, 120.0, LocalDate.of(2025, 2, 10)),
                new Transaction(2L, 102L, 75.0, LocalDate.of(2025, 1, 15))
        );

        when(transactionRepository.saveAll(anyList())).thenReturn(savedTransactionList);
        List<TransactionDTO> result = rewardsService.addTransactions(transactionDTOList);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).getCustomerId());
        assertEquals(102L, result.get(1).getCustomerId());

    }

    @Test
    void testAddTransaction_Failure_NullInput() {
        // Expect Exception
        Exception exception = assertThrows(RewardServiceException.class, () -> {
            rewardsService.addTransaction(null);
        });

        assertEquals("TransactionDTO cannot be null", exception.getMessage());
    }

    @Test
    void testAddTransactions_Failure_EmptyList() {
        // Expect Exception
        Exception exception = assertThrows(RewardServiceException.class, () -> {
            rewardsService.addTransactions(List.of());
        });

        assertEquals("TransactionDTO list cannot be empty", exception.getMessage());
    }


}
