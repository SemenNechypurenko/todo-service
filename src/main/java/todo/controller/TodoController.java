package todo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import todo.dto.TodoRequest;
import todo.dto.TodoResponse;
import todo.service.TodoService;

import java.util.List;

@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
@Slf4j
public class TodoController {

    private final TodoService todoService;

    /**
     * Create a new todo item
     * @param request DTO with description and optional dueDate
     * @return created TodoResponse
     */
    @PostMapping
    public TodoResponse createTodo(@RequestBody TodoRequest request) {
        log.info("Received request to create todo");
        return todoService.createTodo(request);
    }

    /**
     * Get all todos
     * @param all if true, include PAST_DUE items
     * @return list of TodoResponse DTOs
     */
    @GetMapping
    public List<TodoResponse> getTodos(@RequestParam(defaultValue = "false") boolean all) {
        log.info("Received request to get todos, all={}", all);
        return todoService.getTodos(all);
    }

    /**
     * Get a single todo by ID
     */
    @GetMapping("/{id}")
    public TodoResponse getTodo(@PathVariable Long id) {
        log.info("Received request to get todo {}", id);
        return todoService.getTodoById(id);
    }

    /**
     * Update the description of a todo
     */
    @PatchMapping("/{id}/desc")
    public TodoResponse updateDescription(@PathVariable Long id,
                                          @RequestParam String description) {
        log.info("Received request to update description for todo {}", id);
        return todoService.updateDescription(id, description);
    }

    /**
     * Mark a todo as DONE
     */
    @PatchMapping("/{id}/done")
    public TodoResponse markDone(@PathVariable Long id) {
        log.info("Received request to mark todo {} as DONE", id);
        return todoService.markDone(id);
    }

    /**
     * Mark a todo as NOT_DONE
     */
    @PatchMapping("/{id}/undone")
    public TodoResponse markNotDone(@PathVariable Long id) {
        log.info("Received request to mark todo {} as NOT_DONE", id);
        return todoService.markNotDone(id);
    }

}
