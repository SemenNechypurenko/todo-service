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
     * Add new todo item
     */
    @PostMapping
    public TodoResponse createTodo(@RequestBody TodoRequest request) {
        log.info("Create todo request");
        return todoService.createTodo(request);
    }

    /**
     * Get todos
     * default -> only NOT_DONE
     * all=true -> return all
     */
    @GetMapping
    public List<TodoResponse> getTodos(@RequestParam(defaultValue = "false") boolean all) {
        log.info("Get todos request all={}", all);
        return todoService.getTodos(all);
    }

    /**
     * Get single todo
     */
    @GetMapping("/{id}")
    public TodoResponse getTodo(@PathVariable Long id) {
        log.info("Get todo {}", id);
        return todoService.getTodoById(id);
    }

    /**
     * Update description
     */
    @PatchMapping("/{id}/description")
    public TodoResponse updateDescription(
            @PathVariable Long id,
            @RequestParam String description) {

        log.info("Update description for {}", id);
        return todoService.updateDescription(id, description);
    }

    /**
     * Mark todo as DONE
     */
    @PatchMapping("/{id}/done")
    public TodoResponse markDone(@PathVariable Long id) {
        log.info("Mark {} as DONE", id);
        return todoService.markDone(id);
    }

    /**
     * Mark todo as NOT_DONE
     */
    @PatchMapping("/{id}/undone")
    public TodoResponse markNotDone(@PathVariable Long id) {
        log.info("Mark {} as NOT_DONE", id);
        return todoService.markNotDone(id);
    }
}