# Atipera Recruitment Task

This project is written in Java 17 and utilizes Spring Boot 3. Its primary purpose is to retrieve data from the GitHub
API regarding a specified user's repositories, excluding forks.

### Getting Started

To run the project and start the server, follow these steps:

1. Clone the repository to your local machine.
2. Open a terminal and navigate to the project's main directory.
3. Run the following command to start the server:

```
./gradlew bootRun
```

The application will now be accessible on your local machine.

### How to Use

Once the server is up and running, you can access the /repository endpoint by making HTTP GET requests to the following
URL:

> http://localhost:8080/repository?username=someUsername

Replace someUsername with the GitHub username for which you want to fetch repository data.