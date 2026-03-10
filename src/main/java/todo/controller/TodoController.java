package todo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * Creates a new todo item.
     */
    @PostMapping
    public TodoResponse createTodo(@RequestBody TodoRequest request) {

        log.info("Received request to create todo");

        return todoService.createTodo(request);
    }

    /**
     * Retrieves todo items.
     *
     * default -> NOT_DONE
     * all=true -> all items
     */
    @GetMapping
    public List<TodoResponse> getTodos(
            @RequestParam(defaultValue = "false") boolean all) {

        log.info("Received request to get todos (all={})", all);

        return todoService.getTodos(all);
    }

    /**
     * Retrieves a single todo by id.
     */
    @GetMapping("/{id}")
    public TodoResponse getTodo(@PathVariable Long id) {

        log.info("Received request to get todo {}", id);

        return todoService.getTodoById(id);
    }

    /**
     * Updates description.
     */
    @PatchMapping("/{id}/description")
    public TodoResponse updateDescription(
            @PathVariable Long id,
            @RequestParam String description) {

        log.info("Received request to update description for todo {}", id);

        return todoService.updateDescription(id, description);
    }

    /**
     * Marks todo as DONE.
     */
    @PatchMapping("/{id}/done")
    public TodoResponse markDone(@PathVariable Long id) {

        log.info("Received request to mark todo {} as DONE", id);

        return todoService.markDone(id);
    }

    /**
     * Marks todo as NOT_DONE.
     */
    @PatchMapping("/{id}/undone")
    public TodoResponse markNotDone(@PathVariable Long id) {

        log.info("Received request to mark todo {} as NOT_DONE", id);

        return todoService.markNotDone(id);
    }
}