package todo.service;

import org.junit.jupiter.api.BeforeEach;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private TodoItem todo;

    @BeforeEach
    void setUp() {
        todo = TodoItem.builder()
                .id(1L)
                .description("Test todo")
                .status(TodoStatus.NOT_DONE)
                .createdAt(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();
    }

    @Test
    void shouldCreateTodo() {

        TodoRequest request = new TodoRequest();
        request.setDescription("Test todo");
        request.setDueDate(LocalDateTime.now().plusDays(1));

        when(todoRepository.save(any())).thenReturn(todo);

        TodoResponse response = todoService.createTodo(request);

        assertNotNull(response);
        assertEquals("Test todo", response.getDescription());
        verify(todoRepository).save(any());
    }

    @Test
    void shouldThrowExceptionIfDueDateInPast() {

        TodoRequest request = new TodoRequest();
        request.setDescription("Invalid todo");
        request.setDueDate(LocalDateTime.now().minusDays(1));

        assertThrows(InvalidDueDateException.class,
                () -> todoService.createTodo(request));
    }

    @Test
    void shouldReturnTodos() {

        when(todoRepository.findByStatus(TodoStatus.NOT_DONE))
                .thenReturn(List.of(todo));

        List<TodoResponse> todos = todoService.getTodos(false);

        assertEquals(1, todos.size());
    }

    @Test
    void shouldReturnTodoById() {

        when(todoRepository.findById(1L))
                .thenReturn(Optional.of(todo));

        TodoResponse response = todoService.getTodoById(1L);

        assertEquals(1L, response.getId());
    }

    @Test
    void shouldThrowExceptionIfTodoNotFound() {

        when(todoRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(TodoNotFoundException.class,
                () -> todoService.getTodoById(1L));
    }

    @Test
    void shouldUpdateDescription() {

        when(todoRepository.findById(1L))
                .thenReturn(Optional.of(todo));

        TodoResponse response =
                todoService.updateDescription(1L, "Updated");

        assertEquals("Updated", response.getDescription());
    }

    @Test
    void shouldThrowExceptionWhenModifyingPastDueTodo() {

        todo.setStatus(TodoStatus.PAST_DUE);

        when(todoRepository.findById(1L))
                .thenReturn(Optional.of(todo));

        assertThrows(PastDueModificationException.class,
                () -> todoService.updateDescription(1L, "Updated"));
    }

    @Test
    void shouldMarkTodoAsDone() {

        when(todoRepository.findById(1L))
                .thenReturn(Optional.of(todo));

        TodoResponse response = todoService.markDone(1L);

        assertEquals("DONE", response.getStatus());
        assertNotNull(response.getDoneAt());
    }

    @Test
    void shouldMarkTodoAsNotDone() {

        todo.setStatus(TodoStatus.DONE);

        when(todoRepository.findById(1L))
                .thenReturn(Optional.of(todo));

        TodoResponse response = todoService.markNotDone(1L);

        assertEquals("NOT_DONE", response.getStatus());
        assertNull(response.getDoneAt());
    }

    @Test
    void shouldAutomaticallyMarkTodoAsPastDue() {

        todo.setDueDate(LocalDateTime.now().minusHours(1));

        when(todoRepository.findByStatus(TodoStatus.NOT_DONE))
                .thenReturn(List.of(todo));

        List<TodoResponse> todos = todoService.getTodos(false);

        assertEquals("PAST_DUE", todos.get(0).getStatus());

        verify(todoRepository).save(todo);
    }
}