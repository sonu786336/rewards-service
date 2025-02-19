# Rewards API - Spring Boot Application

## 📌 Overview
This API calculates reward points for customers based on their transaction history.

## 🚀 How to Run the Project?
1. Clone the repo:  git clone <your-repo-url> cd rewards-api
2. Run the application: mvn spring-boot:run  
3. Open API Endpoints: 
   - `POST /api/rewards/add` (Add a transaction)
   - (i) Api curl for add single transaction data

        curl --location 'http://localhost:8088/v1/api/rewards/add' \
        --header 'Content-Type: application/json' \
        --data '{
        "customerId":101,
        "amount":120.0,
        "transactionDate" : "2024-02-01"
        }'
     
   - (ii) Api curl for add multiple transaction data

     curl --location 'http://localhost:8088/v1/api/rewards/add-multiple' \
     --header 'Content-Type: application/json' \
     --data '[
     {
     "customerId": 101,
     "amount": 120.0,
     "transactionDate": "2024-02-10"
     },
     {
     "customerId": 101,
     "amount": 75.0,
     "transactionDate": "2024-01-15"
     },
     {
     "customerId": 102,
     "amount": 200.0,
     "transactionDate": "2024-02-05"
     },
     {
     "customerId": 103,
     "amount": 50.0,
     "transactionDate": "2024-01-25"
     },
     {
     "customerId": 104,
     "amount": 95.0,
     "transactionDate": "2024-12-01"
     },
     {
     "customerId": 105,
     "amount": 110.0,
     "transactionDate": "2024-02-20"
     }
     ]'

- `GET /api/rewards` (Calculate reward points)
    - (i) Api curl for rewards points for the months data

    - curl --location 'http://localhost:8088/v1/api/rewards?months=3'
  

## 🔍 API Testing 
- Use **Postman** or `curl` to send requests. 
- Database can be accessed at `http://localhost:8088/h2-console`.

## ✅ Technologies Used 
- Java 17 
- Spring Boot 
- H2 Database 
- JUnit & Mockito (For Testing)  