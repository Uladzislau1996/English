# English bot

This project contains a Telegram bot backed by Spring Boot. The bot now works fully offline and for free: it performs simple local corrections of incoming messages without calling any paid AI or payment services.

## Prerequisites
- Java 17 and Maven

## Run the bot
Provide your Telegram bot token and username in `src/main/resources/application.properties`, then start the application:
```bash
./mvnw spring-boot:run
```

The bot will correct the capitalization and punctuation of any English message and give a short explanation of the changes it made.
