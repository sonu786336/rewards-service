package com.rewardservice.service.impl;

import com.rewardservice.dtos.TransactionDTO;
import com.rewardservice.entity.Transaction;
import com.rewardservice.repository.TransactionRepository;
import com.rewardservice.service.inter_face.RewardsService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
//@RequiredArgsConstructor
public class RewardsServiceImpl implements RewardsService {

    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;

    public RewardsServiceImpl(TransactionRepository transactionRepository, ModelMapper modelMapper) {
        this.transactionRepository = transactionRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Map<Long, Map<String, Integer>> getRewardsPointsForTheMonths(Integer months) {
        if (months == null) {
            months = 3;
        }
        LocalDate[] dateRange = calculateDateRange(months);
        LocalDate startDate = dateRange[0];
        LocalDate endDate = dateRange[1];
        List<Transaction> transactions = fetchTransactions(startDate, endDate);
        return calculateRewardsPoints(transactions);
    }

    @Override
    public TransactionDTO addTransaction(TransactionDTO transactionDTO) {
        Transaction newTransaction = modelMapper.map(transactionDTO, Transaction.class);
        Transaction savedTransaction = transactionRepository.save(newTransaction);
        return modelMapper.map(savedTransaction, TransactionDTO.class);
    }

    @Override
    public List<TransactionDTO> addTransactions(List<TransactionDTO> transactionDTOList) {
        List<Transaction> newTransactions = transactionDTOList
                .stream()
                .map(transactionDTO -> modelMapper.map(transactionDTO, Transaction.class))
                .collect(Collectors.toList());
        return transactionRepository.saveAll(newTransactions)
                .stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .collect(Collectors.toList());

    }

    private LocalDate[] calculateDateRange(Integer months) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusMonths(months).withDayOfMonth(1);
        LocalDate endDate = today.minusMonths(1).withDayOfMonth(today.minusMonths(1).lengthOfMonth());
        System.out.print("Fetching transaction from " + startDate + " to " + endDate);
        return new LocalDate[]{startDate, endDate};
    }

    private List<Transaction> fetchTransactions(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionRepository.findByTransactionDateBetween(startDate, endDate);
        if (transactions.isEmpty()) {
            throw new IllegalArgumentException("No transaction found for the months.");
        }
        return transactions;
    }

    private Map<Long, Map<String, Integer>> calculateRewardsPoints(List<Transaction> transactions) {
        return transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getCustomerId,
                        Collectors.groupingBy(txn -> txn.getTransactionDate().getMonth().toString(),
                                Collectors.summingInt(txn -> calculatePoints(txn.getAmount())))));
    }

    private int calculatePoints(Double amount) {
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("Transaction amount cannot be negative.");
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
