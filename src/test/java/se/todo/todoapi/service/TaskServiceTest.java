package se.todo.todoapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.todo.todoapi.dto.CreateTaskRequest;
import se.todo.todoapi.dto.TaskResponse;
import se.todo.todoapi.entity.*;
import se.todo.todoapi.repository.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(
                taskRepository,
                userRepository,
                categoryRepository
        );
    }

    @Test
    void createTaskSavesTaskWhenUserAndCategoryExist() {
        User user = new User("Timmy", "timmy@example.com");
        Category category = new Category("Work");

        CreateTaskRequest request = new CreateTaskRequest(
                "Pull docker images", "Restart docker", false, 1, 2
        );

        when(userRepository.findById(1))
                .thenReturn(Optional.of(user));

        when(categoryRepository.findById(2))
                .thenReturn(Optional.of(category));

        Task savedTask = new Task(
                "Pull docker images", "Restart docker", false, user, category
        );

        when(taskRepository.save(any(Task.class)))
                .thenReturn(savedTask);

        TaskResponse response = taskService.createTask(request);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());

        Task taskToSave = taskCaptor.getValue();

        assertEquals("Pull docker images", taskToSave.getTitle());
        assertEquals("Restart docker", taskToSave.getDescription());
        assertFalse(taskToSave.isCompleted());
        assertSame(user, taskToSave.getUser());
        assertSame(category, taskToSave.getCategory());

        assertEquals("Pull docker images", response.getTitle());
        assertEquals("Timmy", response.getUserName());
        assertEquals("Work", response.getCategoryName());

    }


}
