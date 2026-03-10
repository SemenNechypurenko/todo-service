/*
 * TodoServiceTest
 *
 * This class contains unit tests for the TodoService.
 * The goal is to verify the business logic of the service layer
 * independently from the database and controller layer.
 *
 * Tested functionality:
 * - creating a todo item
 * - retrieving todo items
 * - retrieving a todo by id
 * - updating description
 * - marking todo as DONE
 * - marking todo as NOT_DONE
 * - validation cases (not found, invalid due date)
 */

package todo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import todo.dto.TodoRequest;
import todo.dto.TodoResponse;
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

    /*
     * Test: createTodo
     *
     * Verifies that a new todo item is created and saved in the repository.
     */
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

    /*
     * Test: createTodo with invalid due date
     *
     * Verifies that an exception is thrown if due date is in the past.
     */
    @Test
    void shouldThrowExceptionIfDueDateInPast() {

        TodoRequest request = new TodoRequest();
        request.setDescription("Invalid todo");
        request.setDueDate(LocalDateTime.now().minusDays(1));

        assertThrows(IllegalArgumentException.class,
                () -> todoService.createTodo(request));
    }

    /*
     * Test: getTodos
     *
     * Verifies that the service returns todo items with NOT_DONE status.
     */
    @Test
    void shouldReturnTodos() {

        when(todoRepository.findByStatus(TodoStatus.NOT_DONE))
                .thenReturn(List.of(todo));

        List<TodoResponse> todos = todoService.getTodos(false);

        assertEquals(1, todos.size());
    }

    /*
     * Test: getTodoById
     *
     * Verifies that a todo item can be retrieved by id.
     */
    @Test
    void shouldReturnTodoById() {

        when(todoRepository.findById(1L))
                .thenReturn(Optional.of(todo));

        TodoResponse response = todoService.getTodoById(1L);

        assertEquals(1L, response.getId());
    }

    /*
     * Test: getTodoById when todo does not exist
     *
     * Verifies that an exception is thrown when the item is not found.
     */
    @Test
    void shouldThrowExceptionIfTodoNotFound() {

        when(todoRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> todoService.getTodoById(1L));
    }

    /*
     * Test: updateDescription
     *
     * Verifies that the description of an existing todo can be updated.
     */
    @Test
    void shouldUpdateDescription() {

        when(todoRepository.findById(1L))
                .thenReturn(Optional.of(todo));

        TodoResponse response =
                todoService.updateDescription(1L, "Updated");

        assertEquals("Updated", response.getDescription());
    }

    /*
     * Test: markDone
     *
     * Verifies that the todo status becomes DONE
     * and completion time is set.
     */
    @Test
    void shouldMarkTodoAsDone() {

        when(todoRepository.findById(1L))
                .thenReturn(Optional.of(todo));

        TodoResponse response = todoService.markDone(1L);

        assertEquals("DONE", response.getStatus());
        assertNotNull(response.getDoneAt());
    }

    /*
     * Test: markNotDone
     *
     * Verifies that the todo status becomes NOT_DONE
     * and completion time is cleared.
     */
    @Test
    void shouldMarkTodoAsNotDone() {

        todo.setStatus(TodoStatus.DONE);

        when(todoRepository.findById(1L))
                .thenReturn(Optional.of(todo));

        TodoResponse response = todoService.markNotDone(1L);

        assertEquals("NOT_DONE", response.getStatus());
        assertNull(response.getDoneAt());
    }
}