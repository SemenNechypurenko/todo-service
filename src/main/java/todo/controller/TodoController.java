package todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import todo.dto.TodoRequest;
import todo.dto.TodoResponse;
import todo.dto.UpdateDescriptionRequest;
import todo.service.TodoService;

import java.util.List;

/**
 * REST controller exposing endpoints for managing Todo items.
 *
 * Supports creating todos, retrieving todos (single or multiple),
 * updating descriptions, and marking todos as DONE or NOT_DONE.
 */
@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
@Slf4j
@Validated
public class TodoController {

    /** Service layer handling the business logic for todos. */
    private final TodoService todoService;

    /**
     * Creates a new todo item.
     *
     * Endpoint: POST /todos
     *
     * Request body is validated using Jakarta Bean Validation.
     *
     * @param request the request body containing todo description and optional dueDate
     * @return the created TodoResponse with HTTP 201 CREATED
     */
    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody TodoRequest request) {

        log.info("Create todo request received");

        TodoResponse response = todoService.createTodo(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Retrieves todo items.
     *
     * Endpoint: GET /todos
     *
     * Default behaviour: returns NOT_DONE todos only.
     * If query parameter all=true, returns all todos regardless of status.
     *
     * @param all flag indicating whether to return all todos
     * @return list of TodoResponse objects with HTTP 200 OK
     */
    @GetMapping
    public ResponseEntity<List<TodoResponse>> getTodos(
            @RequestParam(defaultValue = "false") boolean all) {

        log.info("Get todos request (all={})", all);

        return ResponseEntity.ok(todoService.getTodos(all));
    }

    /**
     * Retrieves a single todo by its ID.
     *
     * Endpoint: GET /todos/{id}
     *
     * @param id the ID of the todo
     * @return the TodoResponse with HTTP 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable Long id) {

        log.info("Get todo by id {}", id);

        return ResponseEntity.ok(todoService.getTodoById(id));
    }

    /**
     * Updates the description of a todo.
     *
     * Endpoint: PATCH /todos/{id}/description
     *
     * The request body must contain a non-blank description.
     * Throws PastDueModificationException if the todo is PAST_DUE.
     *
     * @param id      the ID of the todo
     * @param request request body containing the new description
     * @return the updated TodoResponse with HTTP 200 OK
     */
    @PatchMapping("/{id}/description")
    public ResponseEntity<TodoResponse> updateDescription(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDescriptionRequest request) {

        log.info("Update description for todo {}", id);

        return ResponseEntity.ok(
                todoService.updateDescription(id, request.getDescription())
        );
    }

    /**
     * Marks a todo as DONE.
     *
     * Endpoint: PATCH /todos/{id}/done
     *
     * The operation is idempotent. If the todo is already DONE,
     * the existing doneAt timestamp is preserved.
     *
     * Throws PastDueModificationException if the todo is PAST_DUE.
     *
     * @param id the ID of the todo
     * @return the updated TodoResponse with status DONE and HTTP 200 OK
     */
    @PatchMapping("/{id}/done")
    public ResponseEntity<TodoResponse> markDone(@PathVariable Long id) {

        log.info("Mark todo {} as DONE", id);

        return ResponseEntity.ok(todoService.markDone(id));
    }

    /**
     * Marks a todo as NOT_DONE.
     *
     * Endpoint: PATCH /todos/{id}/undone
     *
     * Throws PastDueModificationException if the todo is PAST_DUE.
     *
     * @param id the ID of the todo
     * @return the updated TodoResponse with status NOT_DONE and HTTP 200 OK
     */
    @PatchMapping("/{id}/undone")
    public ResponseEntity<TodoResponse> markNotDone(@PathVariable Long id) {

        log.info("Mark todo {} as NOT_DONE", id);

        return ResponseEntity.ok(todoService.markNotDone(id));
    }
}