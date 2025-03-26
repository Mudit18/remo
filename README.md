# Tech Stack Used
- **Java**
- **Spring Boot** (using Spring Initializer)
- **PostgreSQL**

## Flow and Design of the Project

## Project Architecture

The project utilizes a layered architecture comprising:

1. **Controller Layer**: Manages HTTP requests and responses, serving as the interface between the client and service layer.
2. **Service Layer**: Contains business logic, processing data from the controller and interacting with the repository for CRUD operations.
3. **Repository Layer**: Handles data access and manipulation via Spring Data JPA.
4. **Model Layer**: Represents data structures, including:
   - **Transaction Model**: Represents financial transactions with attributes like `id`, `userId`, `amount`, `timestamp`, `lastUpdated`, `isActive`, and `transactionType`.
   - **SuspiciousTransaction Model**: Flags suspicious transactions with attributes such as `transaction_id`, `type`, `lastUpdated`, and `resolved`.

### Transaction Processing Workflow

1. A user submits a transaction request via the API.
2. The `TransactionController` delegates the request to the `TransactionService`.
3. The `createTransaction` method saves the transaction and calls `validateSuspiciousTransaction` to check for suspicious criteria.

### Criteria for Flagging Suspicious Transactions

- **High Volume Transactions**: Flags transactions exceeding a predefined amount.
- **Frequent Small Transactions**: Flags if multiple transactions below a certain amount occur within a specified timeframe.
- **Rapid Transfers**: Flags if rapid transfers of a specific type exceed a threshold.

If flagged, a corresponding entry is created in the `SuspiciousTransaction` model for further investigation.

### Alternative Approaches Considered

- **Cron Jobs**: Periodic checks post-processing could delay fraud detection.
- **SQL Triggers**: Automatic flagging at the database level complicates management and may hinder flexibility.
- **Message Queue Architecture**: Asynchronous processing adds complexity and latency, not suitable for initial implementation.

The proactive validation approach was chosen for immediate feedback and effective fraud prevention.

## Assumptions and Trade-offs

- **Immediate Validation vs. Batch Processing**: Immediate validation ensures real-time flagging of suspicious activities, unlike batch processing which may introduce delays.
- **Hardcoded Threshold Values**: Thresholds are hardcoded for simplicity; moving them to configuration files would allow easier modifications.
- **Basic Pagination**: Implemented using an offset parameter, but advanced filtering was not included for simplicity.

## Future Improvements

- **Dynamic Threshold Configuration**: Move thresholds to configuration files for easier adjustments.
- **Authentication and Authorization**: Implement JWT-based authentication for secure access to transactions.
- **Rate Limiting**: Introduce rate limiting to prevent API abuse and ensure fair usage.

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
7. **Access the Swagger UI** to see the API documentation at [http://localhost:8080/remo-api.html](http://localhost:8080/remo-api.html)


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
