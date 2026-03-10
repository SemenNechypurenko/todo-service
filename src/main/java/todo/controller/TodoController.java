package todo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import todo.model.TodoItem;
import todo.service.TodoService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
@Slf4j
public class TodoController {

    private final TodoService todoService;

    /**
     * Create a new todo
     */
    @PostMapping
    public TodoItem createTodo(@RequestParam String description,
                               @RequestParam(required = false) LocalDateTime dueDate) {
        log.info("Received request to create todo");
        return todoService.createTodo(description, dueDate);
    }

    /**
     * Get all todos
     * @param all if true, include PAST_DUE items
     */
    @GetMapping
    public List<TodoItem> getTodos(@RequestParam(defaultValue = "false") boolean all) {
        log.info("Received request to get todos, all={}", all);
        return todoService.getTodos(all);
    }

    /**
     * Get a single todo by id
     */
    @GetMapping("/{id}")
    public TodoItem getTodo(@PathVariable Long id) {
        log.info("Received request to get todo {}", id);
        return todoService.getTodoById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
    }

    /**
     * Update description
     */
    @PatchMapping("/{id}/desc")
    public TodoItem updateDescription(@PathVariable Long id,
                                      @RequestParam String description) {
        log.info("Received request to update description for todo {}", id);
        return todoService.updateDescription(id, description);
    }

    /**
     * Mark todo as DONE
     */
    @PatchMapping("/{id}/done")
    public TodoItem markDone(@PathVariable Long id) {
        log.info("Received request to mark todo {} as DONE", id);
        return todoService.markDone(id);
    }

    /**
     * Mark todo as NOT_DONE
     */
    @PatchMapping("/{id}/undone")
    public TodoItem markNotDone(@PathVariable Long id) {
        log.info("Received request to mark todo {} as NOT_DONE", id);
        return todoService.markNotDone(id);
    }

}
