package todo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todo.dto.TodoRequest;
import todo.dto.TodoResponse;
import todo.service.TodoService;

import java.util.List;

/**
 * REST controller exposing endpoints for managing Todo items.
 * <p>
 * Supports creating todos, retrieving todos (single or multiple),
 * updating descriptions, and marking todos as DONE or NOT_DONE.
 */
@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
@Slf4j
public class TodoController {

    /** Service layer handling the business logic for todos. */
    private final TodoService todoService;

    /**
     * Creates a new todo item.
     * <p>
     * Endpoint: POST /todos
     *
     * @param request the request body containing todo description and optional dueDate
     * @return the created TodoResponse with HTTP 201 CREATED
     */
    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@RequestBody TodoRequest request) {

        log.info("Create todo request received");

        TodoResponse response = todoService.createTodo(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Retrieves todo items.
     * <p>
     * Endpoint: GET /todos
     * Default: returns NOT_DONE todos only.
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
     * <p>
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
     * <p>
     * Endpoint: PATCH /todos/{id}/description
     * <p>
     * Throws PastDueModificationException if the todo is PAST_DUE.
     *
     * @param id          the ID of the todo
     * @param description the new description
     * @return the updated TodoResponse with HTTP 200 OK
     */
    @PatchMapping("/{id}/description")
    public ResponseEntity<TodoResponse> updateDescription(
            @PathVariable Long id,
            @RequestParam String description) {

        log.info("Update description for todo {}", id);

        return ResponseEntity.ok(todoService.updateDescription(id, description));
    }

    /**
     * Marks a todo as DONE.
     * <p>
     * Endpoint: PATCH /todos/{id}/done
     * <p>
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
     * <p>
     * Endpoint: PATCH /todos/{id}/undone
     * <p>
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
