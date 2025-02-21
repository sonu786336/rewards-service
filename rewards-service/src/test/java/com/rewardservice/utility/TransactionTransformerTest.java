package com.rewardservice.utility;

import com.rewardservice.dtos.TransactionDTO;
import com.rewardservice.entity.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TransactionTransformerTest {

    @Test
    public void testToEntity() {
        TransactionDTO transactionDTO = new TransactionDTO(1L, 100.0, LocalDate.of(2023, 1, 1));
        Transaction transaction = TransactionTransformer.toEntity(transactionDTO);

        assertEquals(transactionDTO.getCustomerId(), transaction.getCustomerId());
        assertEquals(transactionDTO.getAmount(), transaction.getAmount());
        assertEquals(transactionDTO.getTransactionDate(), transaction.getTransactionDate());
    }

    @Test
    public void testToDTO() {
        Transaction transaction = new Transaction(1L,1L, 100.0, LocalDate.of(2023, 1, 1));
        TransactionDTO transactionDTO = TransactionTransformer.toDTO(transaction);

        assertEquals(transaction.getCustomerId(), transactionDTO.getCustomerId());
        assertEquals(transaction.getAmount(), transactionDTO.getAmount());
        assertEquals(transaction.getTransactionDate(), transactionDTO.getTransactionDate());
    }
}
