package com.rewardservice;

import com.rewardservice.dtos.TransactionDTO;
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
import static org.mockito.Mockito.*;

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

    @Test
    void testAddTransaction_Success() {
        TransactionDTO transactionDTO = new TransactionDTO(101L, 120.0, LocalDate.of(2025, 2, 10));
        Transaction transaction = new Transaction(null, 101L, 120.0, LocalDate.of(2025, 2, 10));
        Transaction savedTransaction = new Transaction(1L, 101L, 120.0, LocalDate.of(2025, 2, 10));

        when(modelMapper.map(transactionDTO, Transaction.class)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(savedTransaction);
        when(modelMapper.map(savedTransaction, TransactionDTO.class)).thenReturn(transactionDTO);

        TransactionDTO result = rewardsService.addTransaction(transactionDTO);

        assertNotNull(result);
        assertEquals(101L, result.getCustomerId());
        assertEquals(120.0, result.getAmount());
        assertEquals(LocalDate.of(2025, 2, 10), result.getTransactionDate());

        verify(transactionRepository, times(1)).save(transaction);
        verify(modelMapper, times(1)).map(transactionDTO, Transaction.class);
        verify(modelMapper, times(1)).map(savedTransaction, TransactionDTO.class);
    }

    @Test
    void testAddTransactions_Success() {
        // Mock Input Data
        List<TransactionDTO> transactionDTOList = List.of(
                new TransactionDTO(101L, 120.0, LocalDate.of(2025, 2, 10)),
                new TransactionDTO(102L, 75.0, LocalDate.of(2025, 1, 15))
        );

        List<Transaction> transactionList = List.of(
                new Transaction(null, 101L, 120.0, LocalDate.of(2025, 2, 10)),
                new Transaction(null, 102L, 75.0, LocalDate.of(2025, 1, 15))
        );

        List<Transaction> savedTransactionList = List.of(
                new Transaction(1L, 101L, 120.0, LocalDate.of(2025, 2, 10)),
                new Transaction(2L, 102L, 75.0, LocalDate.of(2025, 1, 15))
        );

        // Mock Behavior
        when(modelMapper.map(any(TransactionDTO.class), eq(Transaction.class)))
                .thenAnswer(invocation -> {
                    TransactionDTO dto = invocation.getArgument(0);
                    return new Transaction(null, dto.getCustomerId(), dto.getAmount(), dto.getTransactionDate());
                });

        when(transactionRepository.saveAll(anyList())).thenReturn(savedTransactionList);

        when(modelMapper.map(any(Transaction.class), eq(TransactionDTO.class)))
                .thenAnswer(invocation -> {
                    Transaction txn = invocation.getArgument(0);
                    return new TransactionDTO(txn.getCustomerId(), txn.getAmount(), txn.getTransactionDate());
                });

        // Call Method
        List<TransactionDTO> result = rewardsService.addTransactions(transactionDTOList);

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).getCustomerId());
        assertEquals(102L, result.get(1).getCustomerId());

        // Verify Mock Interactions
        verify(transactionRepository, times(1)).saveAll(anyList());
        verify(modelMapper, times(2)).map(any(TransactionDTO.class), eq(Transaction.class));
        verify(modelMapper, times(2)).map(any(Transaction.class), eq(TransactionDTO.class));
    }

    @Test
    void testAddTransaction_Failure_NullInput() {
        // Expect Exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            rewardsService.addTransaction(null);
        });

        assertEquals("TransactionDTO cannot be null", exception.getMessage());
    }

    @Test
    void testAddTransactions_Failure_EmptyList() {
        // Expect Exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            rewardsService.addTransactions(List.of());
        });

        assertEquals("TransactionDTO list cannot be empty", exception.getMessage());
    }


}
