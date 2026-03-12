# Todo Service

## Service Description

Todo Service is a simple Spring Boot backend that provides a REST API to manage todo items.

The API allows you to:
- create todos
- retrieve todos
- update descriptions
- mark items as done or undone

If a todo passes its due date, the service automatically marks it as **PAST_DUE** when the item is accessed.

---

## Assumptions

- Each todo contains a description, status, creation time, optional due date, and completion time.
- Todos with status **PAST_DUE** cannot be modified.
- Status **PAST_DUE** is applied lazily when the todo is fetched or updated.
- No authentication is required.
- The service uses an **in-memory H2 database**.

---

## Tech Stack

- Java 17
- Spring Boot
- H2 Database
- Maven
- Docker & Docker Compose


---
## Getting Started

Clone the repository:

```bash
git clone https://github.com/SemenNechypurenko/todo-service.git
cd todo-service
```

## How to Build

Build the project with Maven:

```bash
mvn clean package
```

This will compile the code and run all tests.

---

## How to Run Tests

The project includes unit and integration tests for:

- service business logic
- controller endpoints
- validation and error handling

Run tests with:

```bash
mvn test
```

---

## How to Run the Service

The application can be started using Docker Compose:

```bash
docker compose build --no-cache
docker-compose up
```

The API will be available at:

```
http://localhost:8080/todos
```

---

## Continuous Integration (CI)

The project uses **GitHub Actions** for Continuous Integration.

The CI pipeline automatically runs on every:

- push to `master`
- pull request targeting `master`

The pipeline performs the following steps:

- checks out the repository
- sets up **Java 17**
- builds the project
- runs all tests using Maven

This ensures that all tests pass before code changes are merged into the `master` branch.

CI configuration file: .github/workflows/ci.yml


The pipeline runs:

```bash
mvn -B clean test
```

---

# REST API

Base URL:

```
/todos
```

---

## 1. Create Todo

**POST /todos**

Creates a new todo item.

Request body:

```json
{
  "description": "Buy milk",
  "dueDate": "2026-03-12T10:00:00"
}
```

Notes:

- `description` is required
- `dueDate` is optional
- `dueDate` must be in the future

Response:

**201 CREATED**

```json
{
  "id": 1,
  "description": "Buy milk",
  "status": "NOT_DONE",
  "createdAt": "2026-03-10T12:00:00",
  "dueDate": "2026-03-12T10:00:00",
  "doneAt": null
}
```

---

## 2. Get Todos

**GET /todos?all=false**

Returns todo items.

Behavior:

- `all=false` (default) → returns only **NOT_DONE** items
- `all=true` → returns **all items**

Response:

**200 OK**

```json
[
  {
    "id": 1,
    "description": "Buy milk",
    "status": "NOT_DONE",
    "createdAt": "2026-03-10T12:00:00",
    "dueDate": "2030-03-12T10:00:00",
    "doneAt": null
  }
]
```

---

## 3. Get Todo by Id

**GET /todos/{id}**

Returns details of a specific todo.

Response:

**200 OK**

```json
{
  "id": 1,
  "description": "Buy milk",
  "status": "NOT_DONE",
  "createdAt": "2026-03-10T12:00:00",
  "dueDate": "2030-03-12T10:00:00",
  "doneAt": "2026-03-12T12:08:18.2667207"
}
```

---

## 4. Update Description

**PATCH /todos/{id}/description**

Updates the description of a todo.

Request body:

```json
{
  "description": "Buy milk and bread"
}
```

Response:

**200 OK**

```json
{
  "id": 1,
  "description": "Buy milk and bread",
  "status": "NOT_DONE",
  "createdAt": "2026-03-10T12:00:00",
  "dueDate": "2026-03-12T10:00:00",
  "doneAt": null
}
```
---

## 5. Mark Todo as DONE

**PATCH /todos/{id}/done**

Marks the todo as completed.

Response:

**200 OK**

```json
{
"id": 1,
"description": "Buy milk and bread",
"status": "DONE",
"createdAt": "2026-03-10T12:00:00",
"dueDate": "2026-03-12T10:00:00",
"doneAt": null
}
```
---

## 6. Mark Todo as NOT_DONE

**PATCH /todos/{id}/undone**

Marks the todo as not completed.

Response:

**200 OK**

```json
{
"id": 1,
"description": "Buy milk and bread",
"status": "NOT_DONE",
"createdAt": "2026-03-10T12:00:00",
"dueDate": "2026-03-12T10:00:00",
"doneAt": null
}
```
---

## 📝 Postman Collection

A Postman collection is included in the repository to simplify API testing.

File:

```
postman_collection.json
```

### How to Use

1. Open **Postman**
2. Click **Import**
3. Select `todo-service.postman_collection.json`

The collection contains requests for all endpoints:

- Create Todo – `POST /todos`
- Get all Todos – `GET /todos?all=true`
- Get Todo by Id – `GET /todos/{id}`
- Update description – `PATCH /todos/{id}/description`
- Mark as DONE – `PATCH /todos/{id}/done`
- Mark as NOT_DONE – `PATCH /todos/{id}/undone`

Run requests against:

```
http://localhost:8080
```

---

## 🧪 Built-in Tests

Each request in the Postman collection contains automated tests:

- HTTP status validation (200, 201)
- response fields validation (`id`, `status`, `doneAt`)
- business logic checks:
    - new todos are **NOT_DONE**
    - `doneAt` is `null` for incomplete todos

---

## ⚡ Running All Tests

You can run all requests using **Postman Runner**:

1. Open the collection
2. Click **Run**
3. Execute all requests

Postman will run all tests and display the results.

---

💡 **Tip**

Make sure the Spring Boot application is running at:

```
http://localhost:8080
```

before executing the collection.