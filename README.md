# Antigravity Download Manager

A modern, responsive Desktop Download Manager built with **Java 17**, **Spring Boot 3**, and **JavaFX**.

## Features

*   **Modern UI**: Clean dashboard with dark mode styling.
*   **Smart Detection**: Automatically detects providers like Dropbox.
*   **Concurrent Downloads**: Supports multiple simultaneous downloads.
*   **Persistence**: Saves download history using embedded H2 database.
*   **Control**: Pause, Resume, and Cancel downloads.

## Prerequisites

*   Java 17 or higher
*   Maven 3.6+

## How to Run

1.  **Navigate to the project directory**:
    ```bash
    cd C:\Users\rodri\.gemini\antigravity\scratch\download-manager
    ```

2.  **Build the project**:
    ```bash
    mvn clean package
    ```

3.  **Run the application**:
    ```bash
    mvn spring-boot:run
    ```

## Architecture

*   **UI Layer**: JavaFX FXML + Controllers managed by Spring.
*   **Service Layer**: `DownloadService` manages async `DownloadTask`s.
*   **Persistence**: Spring Data JPA with H2.
*   **Core**: Provider detection strategy pattern.

## Troubleshooting

*   If you encounter JavaFX module issues, ensure you are running with a compatible JDK 17+ that includes JavaFX or let Maven handle the dependencies (as configured in `pom.xml`).
