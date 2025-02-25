package com.rewardservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rewardservice.dtos.CustomerData;
import com.rewardservice.dtos.TransactionDTO;
import com.rewardservice.service.transactioneservice.RewardsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for Rewards Controller.
 */
@WebMvcTest(RewardsController.class)
@AutoConfigureMockMvc
class RewardsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RewardsService rewardsService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void testGetRewardPointsForMonths() throws Exception {

        Map<Long, Map<String, Integer>> mockResponse = Map.of(
                101L, Map.of("January", 120, "February", 90)
        );

        when(rewardsService.getRewardsPointsForTheMonths(null, 3)).thenReturn((ResponseEntity<List<CustomerData>>) mockResponse);
        mockMvc.perform(get("/v1/api/rewards?months=3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.101.January").value(120))
                .andExpect(jsonPath("$.101.February").value(90));
    }

    @Test
    void testGetRewardPointsForSpecificCustomer() throws Exception {
        Map<Long, Map<String, Integer>> mockResponse = Map.of(
                104L, Map.of("January", 75, "February", 100)
        );

        when(rewardsService.getRewardsPointsForTheMonths(104L, 3)).thenReturn((ResponseEntity<List<CustomerData>>) mockResponse);

        mockMvc.perform(get("/v1/api/rewards?customerId=104&months=3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.104.January").value(75))
                .andExpect(jsonPath("$.104.February").value(100));
    }










}
