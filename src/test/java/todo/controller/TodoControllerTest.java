package todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import todo.dto.TodoRequest;
import todo.dto.TodoResponse;
import todo.exception.GlobalExceptionHandler;
import todo.model.TodoStatus;
import todo.service.TodoService;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
@Import(GlobalExceptionHandler.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    @Test
    @DisplayName("Should create a todo via POST /todos")
    void shouldCreateTodo() throws Exception {

        TodoRequest request = TodoRequest.builder()
                .description("Test todo")
                .dueDate(Instant.now().plusSeconds(3600))
                .build();

        TodoResponse response = TodoResponse.builder()
                .id(1L)
                .description("Test todo")
                .status(String.valueOf(TodoStatus.NOT_DONE))
                .build();

        when(todoService.createTodo(any())).thenReturn(response);

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Test todo"))
                .andExpect(jsonPath("$.status").value("NOT_DONE"));
    }

    @Test
    @DisplayName("Should return 400 when description is blank")
    void shouldReturn400WhenDescriptionIsBlank() throws Exception {

        TodoRequest request = TodoRequest.builder()
                .description("")
                .dueDate(Instant.now().plusSeconds(3600))
                .build();

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when due date is in the past")
    void shouldReturn400WhenDueDateInPast() throws Exception {

        TodoRequest request = TodoRequest.builder()
                .description("Test todo")
                .dueDate(Instant.now().minusSeconds(3600))
                .build();

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return list of todos")
    void shouldReturnTodos() throws Exception {

        TodoResponse response = TodoResponse.builder()
                .id(1L)
                .description("Test todo")
                .status(String.valueOf(TodoStatus.NOT_DONE))
                .build();

        when(todoService.getTodos(false)).thenReturn(List.of(response));

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Test todo"));
    }

    @Test
    @DisplayName("Should return all todos when all=true parameter is provided")
    void shouldReturnAllTodosWhenAllParamTrue() throws Exception {

        when(todoService.getTodos(true)).thenReturn(List.of());

        mockMvc.perform(get("/todos")
                        .param("all", "true"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return todo by id")
    void shouldReturnTodoById() throws Exception {

        TodoResponse response = TodoResponse.builder()
                .id(1L)
                .description("Test todo")
                .status(String.valueOf(TodoStatus.NOT_DONE))
                .build();

        when(todoService.getTodoById(1L)).thenReturn(response);

        mockMvc.perform(get("/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test todo"));
    }

    @Test
    @DisplayName("Should update todo description")
    void shouldUpdateDescription() throws Exception {

        TodoResponse response = TodoResponse.builder()
                .id(1L)
                .description("Updated")
                .status(String.valueOf(TodoStatus.NOT_DONE))
                .build();

        when(todoService.updateDescription(eq(1L), eq("Updated")))
                .thenReturn(response);

        mockMvc.perform(patch("/todos/1/description")
                        .param("description", "Updated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated"));
    }

    @Test
    @DisplayName("Should return 400 when updating description with blank value")
    void shouldReturn400WhenUpdatingDescriptionWithBlankValue() throws Exception {

        mockMvc.perform(patch("/todos/1/description")
                        .param("description", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should mark todo as DONE")
    void shouldMarkTodoDone() throws Exception {

        TodoResponse response = TodoResponse.builder()
                .id(1L)
                .description("Test todo")
                .status(String.valueOf(TodoStatus.DONE))
                .build();

        when(todoService.markDone(1L)).thenReturn(response);

        mockMvc.perform(patch("/todos/1/done"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    @DisplayName("Should mark todo as NOT_DONE")
    void shouldMarkTodoNotDone() throws Exception {

        TodoResponse response = TodoResponse.builder()
                .id(1L)
                .description("Test todo")
                .status(String.valueOf(TodoStatus.NOT_DONE))
                .build();

        when(todoService.markNotDone(1L)).thenReturn(response);

        mockMvc.perform(patch("/todos/1/undone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NOT_DONE"));
    }
}