# English bot

This project contains a Telegram bot backed by Spring Boot. Use the provided Docker Compose file to spin up the required PostgreSQL instance with the credentials already referenced in `src/main/resources/application.properties`.

## Prerequisites
- Docker and Docker Compose
- Java 17 and Maven (for running the application)

## Start PostgreSQL
```bash
docker compose up -d postgres
```
This starts a PostgreSQL container with:
- database: `english`
- user: `english`
- password: `english`
- port: `5432` (published to localhost)

The container stores data in the `pgdata` volume declared in `docker-compose.yml`.

## Run the bot
After the database is up, start the application:
```bash
./mvnw spring-boot:run
```
Ensure you provide real values for the Telegram, DeepSeek, and NowPayments API keys (update `src/main/resources/application.properties` or use environment variables).
