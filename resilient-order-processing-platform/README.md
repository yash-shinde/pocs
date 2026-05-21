# Resilient Order Processing Platform

Spring Boot project scaffold for an infra-focused order workflow.

## What is included

- Order API
- JPA domain model
- Async workflow events
- Resilience4j retry and circuit breaker hooks
- AOP-based timing and failure metrics
- Correlation ID filter
- Actuator and Prometheus endpoint support
- Docker Compose for PostgreSQL, RabbitMQ, Prometheus, and Grafana

## Start points

- `src/main/java/com/learning/resilientorders/controller/OrderController.java`
- `src/main/java/com/learning/resilientorders/service/OrderService.java`
- `src/main/java/com/learning/resilientorders/service/InventoryService.java`
- `src/main/java/com/learning/resilientorders/service/NotificationService.java`

## Run

1. Start infrastructure from `docker-compose.yml`.
2. Set database environment variables if needed.
3. Run the Spring Boot app with Java 21.

## Integration Test

- Run `OrderFlowIntegrationTest` from IntelliJ, or run the full test suite with Maven if available.

## Messaging Mode

- Default: in-process Spring events with `app.messaging.rabbit-enabled=false`
- RabbitMQ mode: set `app.messaging.rabbit-enabled=true` and start the broker from `docker-compose.yml`

## Grafana

- Grafana is available at `http://localhost:3000`
- Default login is `admin` / `admin`
- The Prometheus datasource and the starter dashboard are provisioned automatically on container start
