# Todo Service

## Service Description
Short description of the service.

## Assumptions
List of assumptions made during implementation.

## Tech Stack
- Java
- Spring Boot
- H2 Database
- Maven
- Docker

## How to Build
...

## How to Run Tests
...

Tests cover:
- business logic of the service layer
- REST controller behavior
- error handling scenarios

## How to Run the Service
...

## REST API
Todo Service API

Base URL
/todos


1. Create a new todo item

POST /todos

Description:
Creates a new todo item with a description and an optional due date.

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
{
"id": 1,
"description": "Buy milk",
"status": "NOT_DONE",
"createdAt": "2026-03-10T12:00:00",
"dueDate": "2026-03-12T10:00:00",
"doneAt": null
}



2. Get todo items

GET /todos?all=false

Description:
Retrieves todo items.

Behavior:
- By default (all=false) returns only items with status NOT_DONE
- If all=true is provided, returns all items regardless of status

Query Parameters:
all (boolean, optional, default=false)

Examples:
GET /todos
GET /todos?all=true

Response:
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



3. Get todo item details

GET /todos/{id}

Description:
Retrieves detailed information about a specific todo item.

Path Parameters:
id (long) — unique identifier of the todo item

Example:
GET /todos/1

Response:
{
"id": 1,
"description": "Buy milk",
"status": "NOT_DONE",
"createdAt": "2026-03-10T12:00:00",
"dueDate": "2026-03-12T10:00:00",
"doneAt": null
}



4. Update todo description

PATCH /todos/{id}/description

Description:
Updates the description of an existing todo item.

Restrictions:
- Items with status PAST_DUE cannot be modified

Path Parameters:
id (long) — unique identifier of the todo item

Query Parameters:
description (string) — new description

Example:
PATCH /todos/1/description?description=Buy bread

Response:
{
"id": 1,
"description": "Buy bread",
"status": "NOT_DONE",
"createdAt": "2026-03-10T12:00:00",
"dueDate": "2026-03-12T10:00:00",
"doneAt": null
}



5. Mark todo as DONE

PATCH /todos/{id}/done

Description:
Marks a todo item as DONE and sets the completion timestamp.

Restrictions:
- Items with status PAST_DUE cannot be modified

Path Parameters:
id (long) — unique identifier of the todo item

Example:
PATCH /todos/1/done

Response:
{
"id": 1,
"description": "Buy bread",
"status": "DONE",
"createdAt": "2026-03-10T12:00:00",
"dueDate": "2026-03-12T10:00:00",
"doneAt": "2026-03-10T12:30:00"
}



6. Mark todo as NOT_DONE

PATCH /todos/{id}/undone

Description:
Marks a todo item as NOT_DONE and clears the completion timestamp.

Restrictions:
- Items with status PAST_DUE cannot be modified

Path Parameters:
id (long) — unique identifier of the todo item

Example:
PATCH /todos/1/undone

Response:
{
"id": 1,
"description": "Buy bread",
"status": "NOT_DONE",
"createdAt": "2026-03-10T12:00:00",
"dueDate": "2026-03-12T10:00:00",
"doneAt": null
}