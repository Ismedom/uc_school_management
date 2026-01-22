# School Management System

A Spring Boot application for managing school operations.

## Docker Setup

The project is containerized using Docker and Docker Compose.

### Prerequisites

- Docker and Docker Compose installed on your machine.
- A `.env` file in the root directory (copy from `.env.example` if available).

### Running the Application

To start the application and the PostgreSQL database:

```bash
docker-compose up --build
```

The application will be available at `http://localhost:8081` (or the port specified in your `.env` file).

### Container Organization

- `docker/Dockerfile`: The multi-stage build file for the Spring Boot app.
- `docker-compose.yml`: Defines the application and database services.
- `.env`: Environment variables for the containers.
