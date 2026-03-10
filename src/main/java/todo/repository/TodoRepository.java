package todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import todo.model.TodoItem;
import todo.model.TodoStatus;

import java.util.List;

public interface TodoRepository extends JpaRepository<TodoItem, Long> {

    List<TodoItem> findByStatusNot(TodoStatus status);

    /**
     * Find all todos with specific status (used for NOT_DONE)
     */
    List<TodoItem> findByStatus(TodoStatus status);
}
