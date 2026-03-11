# Todo Service

## Service Description

Todo Service is a backend application providing a simple REST API to manage todo items.
It allows creating, updating, marking as done/undone, and retrieving todos.

The service automatically marks items as PAST_DUE when their due date has passed,
using lazy evaluation (status is updated when an item is accessed).

---

## Assumptions

- Each todo item has a description, status, creation timestamp, optional due date, and completion timestamp.
- Todos with status PAST_DUE cannot be modified.
- Status changes to PAST_DUE occur lazily upon fetching or updating the todo.
- No authentication is required.
- The service runs with an in-memory H2 database.

---

## Tech Stack

- Java 17
- Spring Boot
- H2 Database
- Maven
- Docker & Docker Compose

---

## How to Build

Build the project using Maven:

```bash
mvn clean package
```
---
This will compile the code and run unit tests.

## How to Run Tests

Unit and integration tests are included to cover:

- Service layer business logic
- Controller behavior
- Error handling (invalid input, PAST_DUE modifications, not found items)

Run tests via Maven:

mvn test

---

## How to Run the Service

The service is dockerized. Run with Docker Compose:

docker-compose up

The API will be available at:

http://localhost:8080/todos

---

# REST API

Base URL:

/todos

The service exposes endpoints to create, read, update, and manage todo items.

---

# 1. Create a new todo item

POST /todos

Description: Creates a new todo item with a description and optional due date.

Request Body:

{
  "description": "string",
  "dueDate": "2026-03-12T10:00:00"
}

Notes:

- description is required
- dueDate is optional
- dueDate must be in the future if provided

Response:

Status: 201 CREATED

{
  "id": 1,
  "description": "Buy milk",
  "status": "NOT_DONE",
  "createdAt": "2026-03-10T12:00:00",
  "dueDate": "2026-03-12T10:00:00",
  "doneAt": null
}

---

# 2. Get todo items

GET /todos?all=false

Description: Retrieves todo items.

Behavior:

- Default (all=false) → returns only items with status NOT_DONE
- all=true → returns all items, including DONE and PAST_DUE

Query Parameters:

Parameter | Type | Default | Description
--------- | ---- | ------- | -----------
all | boolean | false | If true, returns all items

Response:

Status: 200 OK

[
  {
    "id": 1,
    "description": "Buy milk",
    "status": "NOT_DONE",
    "createdAt": "2026-03-10T12:00:00",
    "dueDate": "2026-03-12T10:00:00",
    "doneAt": null
  }
]

---

# 3. Get todo item details

GET /todos/{id}

Description: Retrieves detailed information about a specific todo item.

Path Parameters:

Parameter | Type | Description
--------- | ---- | -----------
id | long | Unique identifier of the todo item

Response:

Status: 200 OK

{
  "id": 1,
  "description": "Buy milk",
  "status": "NOT_DONE",
  "createdAt": "2026-03-10T12:00:00",
  "dueDate": "2026-03-12T10:00:00",
  "doneAt": null
}

---

# 4. Update todo description

PATCH /todos/{id}/description

Description: Updates the description of an existing todo item.

Restrictions:

Items with status PAST_DUE cannot be modified.

Query Parameters:

Parameter | Type | Description
--------- | ---- | -----------
description | string | New description

Response:

Status: 200 OK

{
  "id": 1,
  "description": "Buy bread",
  "status": "NOT_DONE",
  "createdAt": "2026-03-10T12:00:00",
  "dueDate": "2026-03-12T10:00:00",
  "doneAt": null
}

---

# 5. Mark todo as DONE

PATCH /todos/{id}/done

Description: Marks a todo item as DONE and sets the completion timestamp.

Restrictions:

Items with status PAST_DUE cannot be modified.

Response:

Status: 200 OK

{
  "id": 1,
  "description": "Buy bread",
  "status": "DONE",
  "createdAt": "2026-03-10T12:00:00",
  "dueDate": "2026-03-12T10:00:00",
  "doneAt": "2026-03-10T12:30:00"
}

---

# 6. Mark todo as NOT_DONE

PATCH /todos/{id}/undone

Description: Marks a todo item as NOT_DONE and clears the completion timestamp.

Restrictions:

Items with status PAST_DUE cannot be modified.

Response:

Status: 200 OK

{
  "id": 1,
  "description": "Buy bread",
  "status": "NOT_DONE",
  "createdAt": "2026-03-10T12:00:00",
  "dueDate": "2026-03-12T10:00:00",
  "doneAt": null
}

---

# 7. Error Responses

Status | Description
------ | -----------
400 | Validation error (missing description, past dueDate)
404 | Todo not found
409 | Attempt to modify a PAST_DUE todo

