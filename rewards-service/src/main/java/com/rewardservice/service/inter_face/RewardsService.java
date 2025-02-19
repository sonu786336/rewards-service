package com.rewardservice.service.inter_face;

import com.rewardservice.dtos.TransactionDTO;


import java.util.List;
import java.util.Map;

public interface RewardsService {
    Map<Long, Map<String, Integer>> getRewardsPointsForTheMonths(Integer months);

    TransactionDTO addTransaction(TransactionDTO transactionDTO);

    List<TransactionDTO> addTransactions(List<TransactionDTO> transactionDTOList);
}
