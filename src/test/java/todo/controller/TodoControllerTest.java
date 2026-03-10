/*
 * TodoControllerTest
 *
 * This class contains integration-style tests for the REST controller.
 * The controller is tested using MockMvc with the service layer mocked.
 *
 * Tested functionality:
 * - creating a todo item via REST
 * - retrieving todo items
 * - retrieving todo by id
 * - updating description
 * - marking todo as done
 * - marking todo as not done
 */

package todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import todo.dto.TodoRequest;
import todo.dto.TodoResponse;
import todo.service.TodoService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    /*
     * Test: createTodo endpoint
     *
     * Verifies that a POST request creates a todo item.
     */
    @Test
    void shouldCreateTodo() throws Exception {

        TodoRequest request = new TodoRequest();
        request.setDescription("Test");

        TodoResponse response = TodoResponse.builder()
                .id(1L)
                .description("Test")
                .status("NOT_DONE")
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(todoService.createTodo(any()))
                .thenReturn(response);

        mockMvc.perform(post("/todos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Test"));
    }

    /*
     * Test: getTodos endpoint
     *
     * Verifies retrieval of todo items via GET /todos.
     */
    @Test
    void shouldReturnTodos() throws Exception {

        TodoResponse response = TodoResponse.builder()
                .id(1L)
                .description("Test")
                .status("NOT_DONE")
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(todoService.getTodos(false))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Test"));
    }

    /*
     * Test: getTodo by id
     *
     * Verifies retrieval of a specific todo item.
     */
    @Test
    void shouldReturnTodoById() throws Exception {

        TodoResponse response = TodoResponse.builder()
                .id(1L)
                .description("Test")
                .status("NOT_DONE")
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(todoService.getTodoById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    /*
     * Test: updateDescription endpoint
     *
     * Verifies that description can be updated via PATCH request.
     */
    @Test
    void shouldUpdateDescription() throws Exception {

        TodoResponse response = TodoResponse.builder()
                .id(1L)
                .description("Updated")
                .status("NOT_DONE")
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(todoService.updateDescription(1L, "Updated"))
                .thenReturn(response);

        mockMvc.perform(patch("/todos/1/description")
                        .param("description", "Updated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated"));
    }

    /*
     * Test: markDone endpoint
     *
     * Verifies that the todo can be marked as DONE.
     */
    @Test
    void shouldMarkDone() throws Exception {

        TodoResponse response = TodoResponse.builder()
                .id(1L)
                .status("DONE")
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(todoService.markDone(1L))
                .thenReturn(response);

        mockMvc.perform(patch("/todos/1/done"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    /*
     * Test: markNotDone endpoint
     *
     * Verifies that the todo can be marked back to NOT_DONE.
     */
    @Test
    void shouldMarkNotDone() throws Exception {

        TodoResponse response = TodoResponse.builder()
                .id(1L)
                .status("NOT_DONE")
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(todoService.markNotDone(1L))
                .thenReturn(response);

        mockMvc.perform(patch("/todos/1/undone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NOT_DONE"));
    }
}