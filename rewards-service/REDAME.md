# Rewards Service API

## 📌 Project Overview ##
The **Rewards Service API** is a Spring Boot project that calculates **reward points** for customers based on their transactions.
Customers earn points for every dollar spent above a certain threshold.

---

## 📁 Project Structure ##

# rewards-service/
- │── .mvn/
- │── javadocs/
- │── src/
- │ ├── main/
- │ │ ├── java/com/rewardservice/
- │ │ │ ├── appconfig/ # configuration class for application-wide beans.
- │ │ │ ├── controller/ # Handles API requests
- │ │ │ ├── service/ # Business logic implementation
- │ │ │ ├── repository/ # Database interaction (JPA)
- │ │ │ ├── model/ # Entity classes (Transaction)
- │ │ │ ├── dto/ # Data Transfer Objects (DTOs)
- │ │ │ ├── exception/ # Global exception handling
- │ │ ├── resources/ 
- │ │ │ ├── application.properties # App configurations
- │── target
- │── .gitattributes
- │── mvnw 
- │── mvnw.cmd 
- │── pom.xml # Maven dependencies
- │── README.md # Project Documentation

## 🚀 Technologies Used
- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **H2 Database**
- **JUnit & Mockito (for testing)**
- **Maven**

## 🛠 How to Run the Project
1️⃣ **Clone the repository**:
```sh
git clone https://github.com/sonu786336/rewards-service.git
cd rewards-service
```

2️⃣ Run the project using Maven: mvn spring-boot:run

3️⃣ Access API in Postman at: http://localhost:8088/api/rewards

## 📌 Implementation Details
1. Customers earn reward points based on their transactions:
	- $50 or below → 0 Points
	- $51 - $100 → 1 Point per $
	- Above $100 → 2 Points per $
	- Data is stored in H2 database (in-memory DB).
2. Data is stored in H2 database (in-memory DB).
3. Optimistic Locking is enabled using @Version field.

🌍 API Endpoints
- POST :- /api/rewards/add → Add a single transaction
- POST :- /api/rewards/add-multiple → Add multiple transactions
- GET  :- /api/rewards?months=3 → Get reward points for the last X months


🧪 Running Tests: mvn test

📌Example: Add Single Transactions
- POST /api/rewards/add
```json
{
    "customerId": 101,
    "amount": 120.0,
    "transactionDate": "2025-02-10"
}
```

📌 Example: Add Multiple Transactions
- POST /api/rewards/add-multiple
```json
[
  {
    "customerId": 101,
    "amount": 120.0,
    "transactionDate": "2025-02-10"
  },
  {
    "customerId": 101,
    "amount": 75.0,
    "transactionDate": "2025-01-15"
  },
  {
    "customerId": 102,
    "amount": 200.0,
    "transactionDate": "2024-12-05"
  }
]
```

- ✅ Above request will insert multiple or single transactions for different customers.
- ✅ The database will automatically assign unique IDs to these transactions.

📌 ApiCurl: Calculate reward points
```sh
curl --location 'http://localhost:8088/v1/api/rewards?months=3'
```
📌 ApiCurl: Add Single Transactions

```sh	
curl --location 'http://localhost:8088/v1/api/rewards/add' \
        --header 'Content-Type: application/json' \
        --data '{
        "customerId":101,
        "amount":120.0,
        "transactionDate" : "2024-02-01"
        }'
```
		
📌 ApiCurl: Add Multiple Transactions

```sh	
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
```

📌 Contributors
	- Sonu - Developer
