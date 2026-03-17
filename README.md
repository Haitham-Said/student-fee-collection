A simple two‑service Spring Boot project for managing students and fee receipts.

student-service (port 8081): CRUD-style operations for students.
fee-service (port 8082): Collects fees and issues receipts, calling student-service internally.

Tech stack
Java 21
Spring Boot 4.x (web, validation, data JPA)
Database: H2 in‑memory (per service)
Build: Maven
Testing: JUnit 5, Mockito
API docs: Springdoc OpenAPI (/swagger-ui.html)


Configuration
Student service – student-service/src/main/resources/application.yml

Port: 8081
DB: jdbc:h2:mem:studentdb
Console: /h2-console
Fee service – fee-service/src/main/resources/application.yml

Port: 8082
DB: jdbc:h2:mem:feedb
Console: /h2-console
Student service base URL (used by REST client):
