package todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import todo.dto.TodoRequest;
import todo.dto.TodoResponse;
import todo.exception.InvalidDueDateException;
import todo.exception.PastDueModificationException;
import todo.exception.TodoNotFoundException;
import todo.model.TodoItem;
import todo.model.TodoStatus;
import todo.repository.TodoRepository;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

/**
 * Service layer for managing Todo items.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TodoService {

    private final TodoRepository todoRepository;
    private final Clock clock;

    @Transactional
    public TodoResponse createTodo(TodoRequest request) {

        Instant now = clock.instant();

        if (request.getDueDate() != null && request.getDueDate().isBefore(now)) {
            throw new InvalidDueDateException();
        }

        TodoItem todo = TodoItem.builder()
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .status(TodoStatus.NOT_DONE)
                .createdAt(now)
                .build();

        TodoItem saved = todoRepository.save(todo);

        log.info("Created todo {}", saved.getId());

        return mapToResponse(saved);
    }

    public List<TodoResponse> getTodos(boolean all) {

        List<TodoItem> todos = all
                ? todoRepository.findAll()
                : todoRepository.findByStatus(TodoStatus.NOT_DONE);

        todos.forEach(this::updatePastDueStatus);

        return todos.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TodoResponse getTodoById(Long id) {

        TodoItem todo = getTodoOrThrow(id);

        updatePastDueStatus(todo);

        return mapToResponse(todo);
    }

    @Transactional
    public TodoResponse updateDescription(Long id, String description) {

        TodoItem todo = getTodoOrThrow(id);

        updatePastDueStatus(todo);
        forbidPastDue(todo);

        todo.setDescription(description);

        log.info("Updated description for {}", id);

        return mapToResponse(todo);
    }

    /**
     * Idempotent DONE operation.
     */
    @Transactional
    public TodoResponse markDone(Long id) {

        TodoItem todo = getTodoOrThrow(id);

        updatePastDueStatus(todo);
        forbidPastDue(todo);

        if (todo.getStatus() == TodoStatus.DONE) {
            return mapToResponse(todo);
        }

        todo.setStatus(TodoStatus.DONE);
        todo.setDoneAt(clock.instant());

        log.info("Marked {} DONE", id);

        return mapToResponse(todo);
    }

    @Transactional
    public TodoResponse markNotDone(Long id) {

        TodoItem todo = getTodoOrThrow(id);

        updatePastDueStatus(todo);
        forbidPastDue(todo);

        todo.setStatus(TodoStatus.NOT_DONE);
        todo.setDoneAt(null);

        log.info("Marked {} NOT_DONE", id);

        return mapToResponse(todo);
    }

    private TodoItem getTodoOrThrow(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    /**
     * Fix: no longer skips DONE items.
     */
    private void updatePastDueStatus(TodoItem todo) {

        Instant now = clock.instant();

        if (todo.getDueDate() != null &&
                todo.getDueDate().isBefore(now) &&
                todo.getStatus() != TodoStatus.PAST_DUE) {

            todo.setStatus(TodoStatus.PAST_DUE);
            todoRepository.save(todo);

            log.info("Todo {} became PAST_DUE", todo.getId());
        }
    }

    private void forbidPastDue(TodoItem todo) {

        if (todo.getStatus() == TodoStatus.PAST_DUE) {

            log.warn("Attempt to modify PAST_DUE {}", todo.getId());

            throw new PastDueModificationException(todo.getId());
        }
    }

    private TodoResponse mapToResponse(TodoItem todo) {
        return TodoResponse.builder()
                .id(todo.getId())
                .description(todo.getDescription())
                .status(todo.getStatus().name())
                .createdAt(todo.getCreatedAt())
                .dueDate(todo.getDueDate())
                .doneAt(todo.getDoneAt())
                .build();
    }
}