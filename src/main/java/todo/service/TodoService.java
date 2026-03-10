package todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import todo.model.TodoItem;
import todo.model.TodoStatus;
import todo.repository.TodoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor  // Lombok generates constructor for final fields
@Slf4j                   // Lombok adds logger: log.info(), log.warn(), etc.
public class TodoService {

    private final TodoRepository todoRepository;

    /**
     * Create a new todo item
     */
    public TodoItem createTodo(String description, LocalDateTime dueDate) {
        TodoItem todo = TodoItem.builder()
                .description(description)
                .status(TodoStatus.NOT_DONE)  // default status
                .createdAt(LocalDateTime.now())
                .dueDate(dueDate)
                .build();

        TodoItem saved = todoRepository.save(todo);
        log.info("Created new todo with id {}", saved.getId());
        return saved;
    }

    /**
     * Get all todos. If 'all' is false, exclude PAST_DUE items
     */
    public List<TodoItem> getTodos(boolean all) {
        List<TodoItem> todos;
        if (all) {
            todos = todoRepository.findAll();
        } else {
            todos = todoRepository.findByStatusNot(TodoStatus.PAST_DUE);
        }

        // Automatically mark past due todos
        todos.forEach(this::updatePastDueStatus);

        return todos;
    }

    /**
     * Get a todo item by id
     */
    public Optional<TodoItem> getTodoById(Long id) {
        Optional<TodoItem> todoOpt = todoRepository.findById(id);
        todoOpt.ifPresent(this::updatePastDueStatus);
        return todoOpt;
    }

    /**
     * Update the description of a todo
     */
    @Transactional
    public TodoItem updateDescription(Long id, String description) {
        TodoItem todo = getTodoById(id).orElseThrow(() -> new RuntimeException("Todo not found"));
        forbidPastDue(todo);

        todo.setDescription(description);
        log.info("Updated description for todo {}", id);
        return todo;
    }

    /**
     * Mark a todo as DONE
     */
    @Transactional
    public TodoItem markDone(Long id) {
        TodoItem todo = getTodoById(id).orElseThrow(() -> new RuntimeException("Todo not found"));
        forbidPastDue(todo);

        todo.setStatus(TodoStatus.DONE);
        todo.setDoneAt(LocalDateTime.now());
        log.info("Marked todo {} as DONE", id);
        return todo;
    }

    /**
     * Mark a todo as NOT_DONE
     */
    @Transactional
    public TodoItem markNotDone(Long id) {
        TodoItem todo = getTodoById(id).orElseThrow(() -> new RuntimeException("Todo not found"));
        forbidPastDue(todo);

        todo.setStatus(TodoStatus.NOT_DONE);
        todo.setDoneAt(null);
        log.info("Marked todo {} as NOT_DONE", id);
        return todo;
    }

    /**
     * Check and update the status to PAST_DUE if necessary
     */
    private void updatePastDueStatus(TodoItem todo) {
        if (todo.getStatus() != TodoStatus.DONE && todo.getDueDate() != null
                && todo.getDueDate().isBefore(LocalDateTime.now())) {
            todo.setStatus(TodoStatus.PAST_DUE);
            log.info("Todo {} is now PAST_DUE", todo.getId());
        }
    }

    /**
     * Prevent modification of PAST_DUE todos
     */
    private void forbidPastDue(TodoItem todo) {
        if (todo.getStatus() == TodoStatus.PAST_DUE) {
            log.warn("Attempt to modify PAST_DUE todo {}", todo.getId());
            throw new RuntimeException("Cannot modify PAST_DUE todo");
        }
    }

}
