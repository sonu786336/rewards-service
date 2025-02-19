package com.rewardservice.controller;

import com.rewardservice.dtos.TransactionDTO;
import com.rewardservice.service.inter_face.RewardsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
//@RequiredArgsConstructor
@RequestMapping("/v1/api/rewards")
public class RewardsController {
    private final RewardsService rewardsService;

    public RewardsController(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }

    @GetMapping
    public Map<Long, Map<String, Integer>> getRewardsPointsForTheMonths(@RequestParam(required = false) Integer months) {
        return rewardsService.getRewardsPointsForTheMonths(months);
    }

    @PostMapping("/add")
    public TransactionDTO addTransaction(@RequestBody TransactionDTO transactionDTO) {
        return rewardsService.addTransaction(transactionDTO);
    }

    @PostMapping("/add-multiple")
    public List<TransactionDTO> addTransactions(@RequestBody List<TransactionDTO> transactionDTOList) {
        return rewardsService.addTransactions(transactionDTOList);
    }

}
