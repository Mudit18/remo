# Tech Stack Used
- **Java**
- **Spring Boot** (using Spring Initializer)
- **PostgreSQL**

## Flow and Design of the Project

The project follows a layered architecture consisting of the following components:

1. **Controller Layer**: Handles incoming HTTP requests and responses. It acts as an interface between the client and the service layer.
2. **Service Layer**: Contains the business logic of the application. It processes data received from the controller and interacts with the repository layer to perform CRUD operations.
3. **Repository Layer**: Responsible for data access and manipulation. It interacts with the database using Spring Data JPA to perform queries and manage transactions.
4. **Model Layer**: Represents the data structure of the application. It includes entities that map to database tables and data transfer objects (DTOs) for transferring data between layers.
   - **Transaction Model**: Represents a financial transaction with attributes such as `id`, `userId`, `amount`, `timestamp`, `lastUpdated`, `isActive`, and `transactionType`. This model is used to store and retrieve transaction data from the database.
   - **SuspiciousTransaction Model**: Represents a flagged transaction that is considered suspicious based on certain criteria. It includes attributes like `transaction_id`, `type`, `lastUpdated`, and `resolved`. This model is used to track and manage transactions that require further investigation.

The workflow of the transaction processing begins when a user submits a transaction request through the API. The `TransactionController` receives this request and delegates the task to the `TransactionService`. 

In the `TransactionService`, the `createTransaction` method is responsible for creating a new transaction. Once the transaction is saved to the database, the service then calls the `validateSuspiciousTransaction` method to assess whether the transaction meets any criteria for being flagged as suspicious.

A transaction can be flagged suspicious based on several checks:
1. **High Volume Transactions**: If the transaction amount exceeds a predefined threshold, it is flagged as a high volume transaction.
2. **Frequent Small Transactions**: The service checks if there have been multiple transactions below a certain amount within a specified time frame. If the count exceeds a threshold, it indicates potentially suspicious behavior.
3. **Rapid Transfers**: The service checks for rapid transfers of a specific type (e.g., "TRANSFER") within a defined time period. If the count of such transactions exceeds a threshold, it is flagged as suspicious.

If any of these checks are triggered, the transaction is marked as suspicious, and a corresponding entry is created in the `SuspiciousTransaction` model to track it for further investigation. This ensures that potentially fraudulent activities are monitored and addressed promptly.
Other approaches considered for handling suspicious transactions included:

1. **Cron Jobs**: This method would involve scheduling periodic checks to identify suspicious transactions after they have been processed. However, this reactive approach could lead to delays in addressing fraudulent activities, as transactions could remain unflagged for a significant period.

2. **SQL Triggers**: While triggers could automatically flag transactions based on certain criteria at the database level, they can complicate database management and may not provide the flexibility needed for complex business logic. Additionally, triggers can introduce performance overhead during transaction processing.

3. **Message Queue Architecture**: Although a message queue could handle asynchronous processing of transactions and allow for background checks, this approach was not chosen for this implementation due to the need for a fast and straightforward solution. Implementing a message queue would add complexity and latency, which was not desirable for the initial project setup.

Ultimately, the proactive validation approach was selected to ensure immediate feedback and effective fraud prevention without the drawbacks associated with these other methods.

## Assumptions and Trade-offs

- **Immediate Validation vs. Batch Processing**: 
  - Immediate validation of suspicious transactions during the transaction logging process was chosen to ensure suspicious activities are flagged in real time.
  - An alternative approach could have involved using batch processing (e.g., cron jobs), but this might introduce delays in detecting suspicious activity.

- **Threshold Values Hardcoded**: 
  - For simplicity, threshold values (e.g., max transaction amount, frequency check interval) are hardcoded in the application.
  - A more flexible approach would involve moving these to configuration files or environment variables for easier modification.

- **Pagination**: 
  - Basic pagination was added using the offset parameter when fetching suspicious transactions.
  - However, limit or advanced filtering was not implemented to keep the solution straightforward.

## Future Improvements and Enhancements

- **Dynamic Threshold Configuration**: 
  - Move the suspicious transaction thresholds to configuration files or environment variables for easier modification. This will allow for more flexible adjustments without requiring code changes and redeployments.

- **Authentication and Authorization**: 
  - Implement JWT-based authentication to ensure only authorized users can access or create transactions. This will enhance the security of the application and protect sensitive transaction data.

- **Rate Limiting**: 
  - Add rate limiting to prevent abuse of the API. This will help mitigate the risk of denial-of-service attacks and ensure fair usage of the service.


## Setup Instructions

1. **Ensure postgres is installed on your system and run PostgreSQL locally or in Docker and create the `remo` database:**
   ```bash
   docker run --name postgres \
       -e POSTGRES_USER=postgres \
       -e POSTGRES_PASSWORD=postgres \
       -e POSTGRES_DB=remo \
       -p 5432:5432 \
       -d postgres:14
   ```

2. **Check your database connection:**
   ```bash
   psql -h localhost -U postgres -d remo -p 5432
   ```
   Enter the password when prompted.

3. **Update the application properties file** to set the PostgreSQL credentials and connection.
   > **Note:** Consider moving to an environment-specific properties setup.

4. **Run the SQL script** `init.sql`.

5. **Build the project:**
   ```bash
   mvn clean install
   ```

6. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

## Testing

### Sample CURL Commands

- To log a transaction:
   ```bash
   curl -X POST "http://localhost:8080/api/transactions/add" -H "Content-Type: application/json" -d '{
     "userId": "123456",
     "amount": 10,
     "timestamp": "2025-03-26T23:00:00",
     "transactionType": "TRANSFER"
   }'
   ```

- To get suspicious transactions:
   ```bash
   curl -X GET "http://localhost:8080/api/transactions/getSuspiciousTransactions/a123456"
   ```
