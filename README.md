# Tech Stack Used
- **Java**
- **Spring Boot** (using Spring Initializer)
- **PostgreSQL**

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
