package todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import todo.dto.TodoRequest;
import todo.dto.TodoResponse;
import todo.exception.GlobalExceptionHandler;
import todo.exception.InvalidDueDateException;
import todo.exception.PastDueModificationException;
import todo.exception.TodoNotFoundException;
import todo.service.TodoService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
@Import(GlobalExceptionHandler.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Test"));
    }

    @Test
    void shouldReturn400WhenInvalidDueDate() throws Exception {

        TodoRequest request = new TodoRequest();
        request.setDescription("Invalid");
        request.setDueDate(LocalDateTime.now().minusDays(1));

        Mockito.when(todoService.createTodo(any()))
                .thenThrow(new InvalidDueDateException());

        mockMvc.perform(post("/todos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Due date must be in the future"));
    }

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

    @Test
    void shouldReturn404WhenTodoNotFound() throws Exception {

        Mockito.when(todoService.getTodoById(1L))
                .thenThrow(new TodoNotFoundException(1L));

        mockMvc.perform(get("/todos/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Todo with id 1 not found"));
    }

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

    @Test
    void shouldReturn409WhenPastDueModification() throws Exception {

        Mockito.when(todoService.markDone(1L))
                .thenThrow(new PastDueModificationException(1L));

        mockMvc.perform(patch("/todos/1/done"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Todo 1 is PAST_DUE and cannot be modified"));
    }

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