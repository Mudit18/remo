Tech stack used
- Java
- Spring boot (using spring initializer)
- PostgreSQL

1. Run postgres in docker and create the remo DB
docker run --name postgres \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_PASSWORD=postgres \
    -e POSTGRES_DB=remo \
    -p 5432:5432 \
    -d postgres:14

check your db connection using
psql -h localhost -U postgres -d remo -p 5432

enter the password when prompted

2. Update the application properties file to set the postgres credentials and connection

#TODO move to a env specific properties setup

3. Run the SQL script init.sql

4. mvn clean install

5. mvn spring-boot:run

6. 
