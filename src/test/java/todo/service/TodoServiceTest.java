package todo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private Clock clock;

    @InjectMocks
    private TodoService todoService;

    private TodoItem todo;
    private Instant now;

    @BeforeEach
    void setUp() {

        now = Instant.parse("2030-01-01T00:00:00Z");

        lenient().when(clock.instant()).thenReturn(now);

        todo = TodoItem.builder()
                .id(1L)
                .description("Test todo")
                .status(TodoStatus.NOT_DONE)
                .createdAt(now)
                .dueDate(now.plusSeconds(86400))
                .build();
    }

    @Test
    @DisplayName("Should create a new todo successfully")
    void shouldCreateTodo() {

        TodoRequest request = new TodoRequest();
        request.setDescription("Test todo");
        request.setDueDate(now.plusSeconds(86400));

        when(todoRepository.save(any())).thenReturn(todo);

        TodoResponse response = todoService.createTodo(request);

        assertNotNull(response);
        assertEquals("Test todo", response.getDescription());
        assertEquals("NOT_DONE", response.getStatus());

        verify(todoRepository).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidDueDateException when due date is in the past")
    void shouldThrowExceptionIfDueDateInPast() {

        TodoRequest request = new TodoRequest();
        request.setDescription("Invalid todo");
        request.setDueDate(now.minusSeconds(86400));

        assertThrows(
                InvalidDueDateException.class,
                () -> todoService.createTodo(request)
        );
    }

    @Test
    @DisplayName("Should return list of NOT_DONE todos")
    void shouldReturnTodos() {

        when(todoRepository.findByStatus(TodoStatus.NOT_DONE))
                .thenReturn(List.of(todo));

        List<TodoResponse> todos = todoService.getTodos(false);

        assertEquals(1, todos.size());
        assertEquals("NOT_DONE", todos.get(0).getStatus());
        assertEquals("Test todo", todos.get(0).getDescription());
    }

    @Test
    @DisplayName("Should return todo by id")
    void shouldReturnTodoById() {

        when(todoRepository.findById(1L))
                .thenReturn(Optional.of(todo));

        TodoResponse response = todoService.getTodoById(1L);

        assertEquals(1L, response.getId());
        assertEquals("NOT_DONE", response.getStatus());
        assertEquals("Test todo", response.getDescription());
    }

    @Test
    @DisplayName("Should throw TodoNotFoundException when todo does not exist")
    void shouldThrowExceptionIfTodoNotFound() {

        when(todoRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                TodoNotFoundException.class,
                () -> todoService.getTodoById(1L)
        );
    }

    @Test
    @DisplayName("Should update todo description")
    void shouldUpdateDescription() {

        when(todoRepository.findById(1L))
                .thenReturn(Optional.of(todo));

        TodoResponse response =
                todoService.updateDescription(1L, "Updated");

        assertEquals("Updated", response.getDescription());
    }

    @Test
    @DisplayName("Should throw PastDueModificationException when modifying a past due todo")
    void shouldThrowExceptionWhenModifyingPastDueTodo() {

        todo.setStatus(TodoStatus.PAST_DUE);

        when(todoRepository.findById(1L))
                .thenReturn(Optional.of(todo));

        assertThrows(
                PastDueModificationException.class,
                () -> todoService.updateDescription(1L, "Updated")
        );
    }

    @Test
    @DisplayName("Should mark todo as DONE and set doneAt timestamp")
    void shouldMarkTodoAsDone() {

        when(todoRepository.findById(1L))
                .thenReturn(Optional.of(todo));

        TodoResponse response = todoService.markDone(1L);

        assertEquals("DONE", response.getStatus());
        assertNotNull(response.getDoneAt());
        assertEquals("Test todo", response.getDescription());
    }

    @Test
    @DisplayName("Should mark todo as NOT_DONE and clear doneAt timestamp")
    void shouldMarkTodoAsNotDone() {

        todo.setStatus(TodoStatus.DONE);
        todo.setDoneAt(now);

        when(todoRepository.findById(1L))
                .thenReturn(Optional.of(todo));

        TodoResponse response = todoService.markNotDone(1L);

        assertEquals("NOT_DONE", response.getStatus());
        assertNull(response.getDoneAt());
        assertEquals("Test todo", response.getDescription());
    }

    @Test
    @DisplayName("Should automatically mark todo as PAST_DUE when due date has passed")
    void shouldAutomaticallyMarkTodoAsPastDue() {

        todo.setDueDate(now.minusSeconds(3600));

        when(todoRepository.findByStatus(TodoStatus.NOT_DONE))
                .thenReturn(List.of(todo));

        List<TodoResponse> todos = todoService.getTodos(false);

        assertEquals("PAST_DUE", todos.get(0).getStatus());
        assertEquals("Test todo", todos.get(0).getDescription());

        verify(todoRepository).save(todo);
    }
}