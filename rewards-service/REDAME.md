# Rewards API - Spring Boot Application

## 📌 Overview
This API calculates reward points for customers based on their transaction history.

## 🚀 How to Run the Project?
1. Clone the repo:  git clone <your-repo-url> cd rewards-api
2. Run the application: mvn spring-boot:run  
3. Open API Endpoints: 
- `POST /api/rewards/add` (Add a transaction) 
- `GET /api/rewards` (Calculate reward points)

## 🔍 API Testing 
- Use **Postman** or `curl` to send requests. 
- Database can be accessed at `http://localhost:8088/h2-console`.

## ✅ Technologies Used 
- Java 17 
- Spring Boot 
- H2 Database 
- JUnit & Mockito (For Testing)  