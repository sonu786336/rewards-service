package com.rewardservice.transformer;

import com.rewardservice.dtos.TransactionDTO;
import com.rewardservice.entity.Transaction;

/**
 * Utility class for transforming between Transaction and TransactionDTO objects.
 */
public class TransactionTransformer {

    /**
     * Converts a TransactionDTO object to a Transaction entity.
     *
     * @param transactionDTO the TransactionDTO object to convert
     * @return the converted Transaction entity
     */
    public static Transaction toEntity(TransactionDTO transactionDTO){
        return new Transaction(null, transactionDTO.getCustomerId(), transactionDTO.getAmount(), transactionDTO.getTransactionDate());
    }

    /**
     * Converts a Transaction entity to a TransactionDTO object.
     *
     * @param transaction the Transaction entity to convert
     * @return the converted TransactionDTO object
     */
    public static TransactionDTO toDTO(Transaction transaction){
        return new TransactionDTO(transaction.getCustomerId(), transaction.getAmount(), transaction.getTransactionDate());
    }
}
