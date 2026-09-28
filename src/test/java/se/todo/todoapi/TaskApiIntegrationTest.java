package se.todo.todoapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import se.todo.todoapi.dto.CreateTaskRequest;
import se.todo.todoapi.dto.TaskResponse;
import se.todo.todoapi.dto.UpdateTaskRequest;
import se.todo.todoapi.entity.Task;
import se.todo.todoapi.entity.Category;
import se.todo.todoapi.entity.User;
import se.todo.todoapi.repository.CategoryRepository;
import se.todo.todoapi.repository.TaskRepository;
import se.todo.todoapi.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class TaskApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private RestClient client;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        client = RestClient.create("http://localhost:" + port);
    }

    @Test
    void createdTaskCanBeFetched() {
        User user = userRepository.save(
                new User("Test User", "test@example.com")
        );
        Category category = categoryRepository.save(
                new Category("School")
        );

        CreateTaskRequest request = new CreateTaskRequest(
                "Write integration test",
                "Verify creation and retrieval",
                false,
                user.getId(),
                category.getId()
        );

        var createResponse = client.post()
                .uri("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(TaskResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        TaskResponse created = createResponse.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getId()).isPositive();

        var getResponse = client.get()
                .uri("/api/tasks/{id}", created.getId())
                .retrieve()
                .toEntity(TaskResponse.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        TaskResponse fetched = getResponse.getBody();
        assertThat(fetched).isNotNull();
        assertThat(fetched.getId()).isEqualTo(created.getId());
        assertThat(fetched.getTitle()).isEqualTo(request.getTitle());
        assertThat(fetched.getDescription()).isEqualTo(request.getDescription());
        assertThat(fetched.isCompleted()).isFalse();
        assertThat(fetched.getUserId()).isEqualTo(user.getId());
        assertThat(fetched.getUserName()).isEqualTo(user.getName());
        assertThat(fetched.getCategoryId()).isEqualTo(category.getId());
        assertThat(fetched.getCategoryName()).isEqualTo(category.getName());
    }

    @Test
    void updatedTaskCanBeFetched() {
        User user = userRepository.save(
                new User("Test User", "test@example.com")
        );
        Category category = categoryRepository.save(
                new Category("School")
        );
        Task task = taskRepository.save(
                new Task("Old title", "Old description", false, user, category)
        );

        UpdateTaskRequest request = new UpdateTaskRequest(
                "Updated title",
                "Updated description",
                true,
                user.getId(),
                category.getId()
        );

        var updateResponse = client.put()
                .uri("/api/tasks/{id}", task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(TaskResponse.class);

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        TaskResponse updated = updateResponse.getBody();
        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(task.getId());
        assertThat(updated.getTitle()).isEqualTo(request.getTitle());
        assertThat(updated.getDescription()).isEqualTo(request.getDescription());
        assertThat(updated.isCompleted()).isTrue();

        var getResponse = client.get()
                .uri("/api/tasks/{id}", task.getId())
                .retrieve()
                .toEntity(TaskResponse.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        TaskResponse fetched = getResponse.getBody();
        assertThat(fetched).isNotNull();
        assertThat(fetched.getId()).isEqualTo(task.getId());
        assertThat(fetched.getTitle()).isEqualTo(request.getTitle());
        assertThat(fetched.getDescription()).isEqualTo(request.getDescription());
        assertThat(fetched.isCompleted()).isTrue();
        assertThat(fetched.getUserId()).isEqualTo(user.getId());
        assertThat(fetched.getCategoryId()).isEqualTo(category.getId());
    }

    @Test
    void deletedTaskIsRemovedFromDatabase() {
        User user = userRepository.save(
                new User("Test User", "test@example.com")
        );
        Category category = categoryRepository.save(
                new Category("School")
        );

        Task task = taskRepository.save(
                new Task("Delete this task", "Test deletion", false, user, category)
        );
        Task otherTask = taskRepository.save(
                new Task("Keep this task", "Should remain", false, user, category)
        );

        var deleteResponse = client.delete()
                .uri("/api/tasks/{id}", task.getId())
                .retrieve()
                .toBodilessEntity();

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(taskRepository.existsById(task.getId())).isFalse();
        assertThat(taskRepository.existsById(otherTask.getId())).isTrue();
    }
}