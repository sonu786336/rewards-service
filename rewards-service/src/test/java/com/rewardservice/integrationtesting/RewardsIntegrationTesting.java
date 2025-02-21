package com.rewardservice.integrationtesting;

import com.rewardservice.dtos.TransactionDTO;
import com.rewardservice.entity.Transaction;
import com.rewardservice.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for RewardsService.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RewardsIntegrationTesting {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeEach
    void setUp(){
        transactionRepository.saveAll(List.of( new Transaction(null,104L,120.0,LocalDate.of(2025,2,10)),
                new Transaction(null,104L,75.0,LocalDate.of(2025,1,15))));

    }

    @Test
    void testGetRewardPointsForDefault3Months(){
        ResponseEntity<String> response = testRestTemplate
                .getForEntity("/v1/api/rewards", String.class);
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }
    @Test
    void testGetRewardPointsForAnyNumberOfMonths(){
        ResponseEntity<String> response = testRestTemplate
                .getForEntity("/v1/api/rewards?months = 3", String.class);
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    void testGetRewardPointsForTheMonthsForSpecificCustomerId(){
        ResponseEntity<String> response = testRestTemplate
                .getForEntity("/v1/api/rewards?customerId=104", String.class);
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    void testGetRewardPointsForTheSpecificMonthsAndForTheSpecificCustomerId(){
        ResponseEntity<String> response = testRestTemplate
                .getForEntity("/v1/api/rewards?months=3&customerId=104", String.class);
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    void testTransactionSuccess(){
        TransactionDTO transactionDTO = new TransactionDTO(101L, 120.0, LocalDate.of(2025, 2, 10));
        ResponseEntity<TransactionDTO> response = testRestTemplate
                .postForEntity("/v1/api/rewards/add", new HttpEntity<>(transactionDTO,getHeaders()),TransactionDTO.class);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(101L,response.getBody().getCustomerId());
    }

    @Test
    void testTransactionsSuccess(){
        List<TransactionDTO> transactionDTOList = List.of(
                new TransactionDTO(101L, 120.0, LocalDate.of(2025, 2, 10)),
                new TransactionDTO(102L, 75.0, LocalDate.of(2025, 1, 15))
        );

        ResponseEntity<TransactionDTO[]> response = testRestTemplate
                .postForEntity("/v1/api/rewards/add-multiple", new HttpEntity<>(transactionDTOList,getHeaders()),TransactionDTO[].class);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2,response.getBody().length);
        assertEquals(101L,response.getBody()[0].getCustomerId());
        assertEquals(102L,response.getBody()[1].getCustomerId());
    }



    private HttpHeaders getHeaders(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }


}
