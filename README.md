# Elect Shop API

## Overview

This is a Java Backend Technical Assessment project. Built with Spring Boot, this project aims to provide a comprehensive e-commerce platform with features such as user authentication, product management, and order processing as the pdf file describes.

### Technologies Used

- **Java21**: The primary programming language used for the backend.
- **Gradle**: For build automation and dependency management.
- **Spring Boot**: For building the RESTful API.
- **Spring JPA**: For database interactions and ORM.
- **JWT**: For secure user authentication.
- **OpenAPI**: For API documentation and server/client stub generation.
- **Lombok**: For reducing boilerplate code in Java.
- **Redis**: For caching and improving performance.

### Source structure

```bash
elect-shop
 ┣ docker
 ┃ ┗ docker-compose.yaml
 ┣ gradle
 ┃ ┗ wrapper
 ┃ ┃ ┣ gradle-wrapper.jar # version 8.14.3
 ┃ ┃ ┗ gradle-wrapper.properties
 ┣ openapi
 ┃ ┣ output # Contain generated server stub
 ┃ ┗ specs.yaml # API definition
 ┣ src
 ┃ ┣ main
 ┃ ┃ ┣ java
 ┃ ┃ ┃ ┗ me
 ┃ ┃ ┃ ┃ ┗ huypc
 ┃ ┃ ┃ ┃ ┃ ┗ elect_shop
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ config # Contain application configuration
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ controller # Contain request handling logic
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ entity # Contain database entities
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ exception # Contain custom exceptions
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ generated # Contain generated code
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ api
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ repository # Contain data access logic
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ seed # Contain data seeding logic, run when starting application
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ service # Contain business logic
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ ElectShopApplication.java # Entry point of the application
 ┃ ┃ ┗ resources
 ┃ ┃ ┃ ┗ application.properties # Application configuration properties
 ┃ ┗ test
 ┃ ┃ ┗ java
 ┃ ┃ ┃ ┗ me
 ┃ ┃ ┃ ┃ ┗ huypc
 ┃ ┃ ┃ ┃ ┃ ┗ elect_shop
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ service # Contain service layer tests
 ┣ .gitattributes
 ┣ .gitignore
 ┣ Altech - Java Backend Technical Assessment_2025.pdf
 ┣ README.md
 ┣ build.gradle
 ┣ gradlew
 ┣ gradlew.bat
 ┗ settings.gradle
```

### Useful commands

- **Run the application**: `./gradlew bootRun`
- **Build the project**: `./gradlew build`
- **Run all tests**: `./gradlew test`
- **Run a specific test class**: `./gradlew test --tests "me.huypc.elect_shop.service.AddToCartTest"`
- **Generate OpenAPI documentation**: `./gradlew clean build -DskipTests -DgenerateOpenApiDocs`
- **Generate server stub**: `./gradlew openApiGenerate` (the output will be located in `./openapi/output`)

**Note**: Please check the `docker` dir for Docker-related files and configurations.

### Test
Before running tests, we need to create a test database named `elect_shop_test` in the database server, then run the test command in the [Useful commands](#useful-commands) section.
