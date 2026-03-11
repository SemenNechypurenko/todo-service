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

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for managing Todo items.
 * <p>
 * Provides methods for creating, updating, retrieving, and marking todos as DONE or NOT_DONE.
 * Handles business rules such as lazy PAST_DUE status updates and preventing modification of past-due todos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TodoService {

    /** Repository for accessing TodoItem entities in the database. */
    private final TodoRepository todoRepository;

    /**
     * Creates a new todo item.
     * <p>
     * Sets status to NOT_DONE and records the creation timestamp.
     * Validates that the optional dueDate is in the future.
     *
     * @param request the DTO containing description and optional dueDate
     * @return the created TodoResponse
     * @throws InvalidDueDateException if dueDate is in the past
     */
    @Transactional
    public TodoResponse createTodo(TodoRequest request) {

        LocalDateTime now = LocalDateTime.now();

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

    /**
     * Retrieves todos from the database.
     * <p>
     * Applies lazy update to PAST_DUE status if needed.
     *
     * @param all if true, returns all todos; otherwise, only NOT_DONE todos
     * @return list of TodoResponse objects
     */
    public List<TodoResponse> getTodos(boolean all) {

        List<TodoItem> todos = all
                ? todoRepository.findAll()
                : todoRepository.findByStatus(TodoStatus.NOT_DONE);

        // Update PAST_DUE status lazily
        todos.forEach(this::updatePastDueStatus);

        return todos.stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Retrieves a single todo by ID.
     * <p>
     * Updates PAST_DUE status if necessary.
     *
     * @param id the ID of the todo
     * @return the corresponding TodoResponse
     * @throws TodoNotFoundException if no todo exists with the given ID
     */
    public TodoResponse getTodoById(Long id) {

        TodoItem todo = getTodoOrThrow(id);

        updatePastDueStatus(todo);

        return mapToResponse(todo);
    }

    /**
     * Updates the description of a todo.
     * <p>
     * Throws exception if the todo is PAST_DUE.
     *
     * @param id          the ID of the todo
     * @param description the new description
     * @return the updated TodoResponse
     * @throws PastDueModificationException if the todo is PAST_DUE
     */
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
     * Marks a todo as DONE.
     * <p>
     * Throws exception if the todo is PAST_DUE.
     *
     * @param id the ID of the todo
     * @return the updated TodoResponse with status DONE
     * @throws PastDueModificationException if the todo is PAST_DUE
     */
    @Transactional
    public TodoResponse markDone(Long id) {

        TodoItem todo = getTodoOrThrow(id);

        updatePastDueStatus(todo);
        forbidPastDue(todo);

        todo.setStatus(TodoStatus.DONE);
        todo.setDoneAt(LocalDateTime.now());

        log.info("Marked {} DONE", id);

        return mapToResponse(todo);
    }

    /**
     * Marks a todo as NOT_DONE.
     * <p>
     * Throws exception if the todo is PAST_DUE.
     *
     * @param id the ID of the todo
     * @return the updated TodoResponse with status NOT_DONE
     * @throws PastDueModificationException if the todo is PAST_DUE
     */
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

    /**
     * Retrieves a todo by ID or throws a TodoNotFoundException if not found.
     *
     * @param id the ID of the todo
     * @return the corresponding TodoItem
     * @throws TodoNotFoundException if no todo exists with the given ID
     */
    private TodoItem getTodoOrThrow(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    /**
     * Lazily updates the status of a todo to PAST_DUE if its due date has passed.
     * <p>
     * Does nothing if the todo is already DONE or has no due date.
     *
     * @param todo the TodoItem to check and update
     */
    private void updatePastDueStatus(TodoItem todo) {

        if (todo.getStatus() == TodoStatus.DONE) {
            return;
        }

        if (todo.getDueDate() != null &&
                todo.getDueDate().isBefore(LocalDateTime.now())) {

            todo.setStatus(TodoStatus.PAST_DUE);
            todoRepository.save(todo);

            log.info("Todo {} became PAST_DUE", todo.getId());
        }
    }

    /**
     * Prevents modification of a todo that is PAST_DUE.
     *
     * @param todo the TodoItem to check
     * @throws PastDueModificationException if the todo is PAST_DUE
     */
    private void forbidPastDue(TodoItem todo) {

        if (todo.getStatus() == TodoStatus.PAST_DUE) {

            log.warn("Attempt to modify PAST_DUE {}", todo.getId());

            throw new PastDueModificationException(todo.getId());
        }
    }

    /**
     * Maps a TodoItem entity to a TodoResponse DTO.
     *
     * @param todo the TodoItem entity
     * @return the corresponding TodoResponse
     */
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
