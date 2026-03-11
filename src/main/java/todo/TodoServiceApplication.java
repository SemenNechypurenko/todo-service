package todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Todo Service Spring Boot application.
 * <p>
 * Starts the embedded server and initializes the application context.
 */
@SpringBootApplication
public class TodoServiceApplication {

    /**
     * Launches the Spring Boot application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SpringApplication.run(TodoServiceApplication.class, args);
    }
}