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
 */
@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
@Slf4j
public class TodoController {

    private final TodoService todoService;

    /**
     * Create a new todo item.
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
     * Get todo items.
     * Default: returns NOT_DONE items only.
     * If all=true: returns all items.
     */
    @GetMapping
    public ResponseEntity<List<TodoResponse>> getTodos(
            @RequestParam(defaultValue = "false") boolean all) {

        log.info("Get todos request (all={})", all);

        return ResponseEntity.ok(todoService.getTodos(all));
    }

    /**
     * Get todo by id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable Long id) {

        log.info("Get todo by id {}", id);

        return ResponseEntity.ok(todoService.getTodoById(id));
    }

    /**
     * Update todo description.
     */
    @PatchMapping("/{id}/description")
    public ResponseEntity<TodoResponse> updateDescription(
            @PathVariable Long id,
            @RequestParam String description) {

        log.info("Update description for todo {}", id);

        return ResponseEntity.ok(todoService.updateDescription(id, description));
    }

    /**
     * Mark todo as DONE.
     */
    @PatchMapping("/{id}/done")
    public ResponseEntity<TodoResponse> markDone(@PathVariable Long id) {

        log.info("Mark todo {} as DONE", id);

        return ResponseEntity.ok(todoService.markDone(id));
    }

    /**
     * Mark todo as NOT_DONE.
     */
    @PatchMapping("/{id}/undone")
    public ResponseEntity<TodoResponse> markNotDone(@PathVariable Long id) {

        log.info("Mark todo {} as NOT_DONE", id);

        return ResponseEntity.ok(todoService.markNotDone(id));
    }
}