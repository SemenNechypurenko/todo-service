package todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import todo.model.TodoItem;
import todo.model.TodoStatus;

import java.util.List;

/**
 * Repository for accessing Todo items in the database.
 *
 * Provides methods to:
 * - find todos by id (inherited from JpaRepository)
 * - find todos by status
 * - find todos excluding specific status
 *
 * No custom implementations are needed since Spring Data JPA handles queries automatically.
 */
public interface TodoRepository extends JpaRepository<TodoItem, Long> {

    /**
     * Finds all todos with the specific status.
     * Useful for retrieving NOT_DONE todos.
     *
     * @param status desired status
     * @return list of todos with the given status
     */
    List<TodoItem> findByStatus(TodoStatus status);
}