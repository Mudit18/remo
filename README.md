# Remo

## Description
Transaction recording and suspicious activity pattern detection

## Requirements
For detailed requirements, refer to the [PRD here](https://docs.google.com/document/d/16FhEzKgQoMVkfY8WiZpQ3oNoooFEHIF58zbdLFXzt2s/edit?tab=t.0#heading=h.ft9vvjr9c5r7).

# Table of Contents
1. [Setup Instructions](#setup-instructions)
   - [DB Setup](#db-setup)
   - [Build the project](#build-the-project)
   - [Testing](#testing)
2. [Infrastructure](#infrastructure)
   - [Tech Stack Used](#tech-stack-used)
   - [Project Architecture](#project-architecture)
   - [Transaction Processing Workflow](#transaction-processing-workflow)
   - [Criteria for Flagging Suspicious Transactions](#criteria-for-flagging-suspicious-transactions)
   - [Alternative Approaches Considered](#alternative-approaches-considered)
   - [Assumptions and Tradeoffs](#assumptions-and-tradeoffs)
3. [Future Improvements](#future-improvements)


<br><br>

# Setup Instructions

## DB Setup

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
   ```bash
   psql -h localhost -U postgres -d remo -p 5432 -f src/scripts/db/init.sql
   ```

<br>

## Build the project

1. **Build the project:**
   ```bash
   mvn clean install
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```
3. **Access the [Swagger UI](http://localhost:8080/remo-api.html)** to see the API documentation at

<br>

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

- To get users blocked via suspicious transactions
   ```bash
   curl -X GET "http://localhost:8080/api/transactions/getSuspiciousTransactions/a123456"
   ```

<br>
<br>

# Infrastructure

## Tech Stack Used
- **Java**
- **Spring Boot** (using Spring Initializer)
- **PostgreSQL**

<br>

## Project Architecture

The project utilizes a layered architecture comprising:

1. **Controller Layer**: Manages HTTP requests and responses, serving as the interface between the client and service layer.
2. **Service Layer**: Contains business logic, processing data from the controller and interacting with the repository for CRUD operations.
3. **Repository Layer**: Handles data access and manipulation via Spring Data JPA.
4. **Model Layer**: Represents data structures, including:
   - **Transaction Model**: Represents financial transactions with attributes like `id`, `userId`, `amount`, `timestamp`, `lastUpdated`, `isActive`, and `transactionType`.
   - **SuspiciousTransaction Model**: Flags suspicious transactions with attributes such as `transaction_id`, `type`, `lastUpdated`, and `resolved`.

<br>

### Transaction Processing Workflow

1. A user submits a transaction request via the API.
2. The `TransactionController` delegates the request to the `TransactionService`.
3. The `createTransaction` method saves the transaction and calls `validateSuspiciousTransaction` to check for suspicious criteria.

<br>

### Criteria for Flagging Suspicious Transactions

- **High Volume Transactions**: Flags transactions exceeding a predefined amount.
- **Frequent Small Transactions**: Flags if multiple transactions below a certain amount occur within a specified timeframe.
- **Rapid Transfers**: Flags if rapid transfers of a specific type exceed a threshold.

If flagged, a corresponding entry is created in the `SuspiciousTransaction` model for further investigation.

<br>

### Alternative Approaches Considered

- **Cron Jobs**: Periodic checks post-processing could delay fraud detection.
- **SQL Triggers**: Automatic flagging at the database level complicates management and may hinder flexibility.
- **Message Queue Architecture**: Asynchronous processing adds complexity and latency, not suitable for initial implementation.

The proactive validation approach was chosen considering the time to build and the accuracy.

<br>

### Assumptions and Tradeoffs

- The transactions coming to the system can be from past but are always sent in order of their timestamp.
- Flagging suspicious transactions is considered the most important factor, so the transactions have been kept transactional in nature - if validation fails, system doesn't allow the transaction.

<br>
<br>

## Future Improvements

- **Dynamic Threshold Configuration**: Move thresholds to configuration files for easier adjustments.
- **Authentication and Authorization**: Implement JWT-based authentication for secure access to transactions.
- **Rate Limiting**: Introduce rate limiting to prevent API abuse and ensure fair usage.
- **Database Indexing**: Implement indexing on frequently queried fields like user_id and timestamp in the database to improve query performance and efficiency.
