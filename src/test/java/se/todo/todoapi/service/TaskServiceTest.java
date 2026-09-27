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
import static org.mockito.Mockito.never;

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
        User user = new User("Timmy", "timmy@example.se");
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

    @Test
    void createTaskThrowsWhenUserDoesNotExist() {
        CreateTaskRequest request = new CreateTaskRequest(
                "Pull docker images", "Restart docker", false, 999, 2
        );
        when(userRepository.findById(999))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskService.createTask(request)
        );

        assertEquals("User not found", exception.getMessage());

        verify(taskRepository, never()).save(any(Task.class));

    }

    @Test
    void createTaskThrowsWhenCategoryDoesNotExist() {
        User user = new User("Timmy", "timmy@example.se");

        CreateTaskRequest request = new CreateTaskRequest(
                "Pull docker images", "Restart docker", false, 1, 999
        );

        when(userRepository.findById(1))
                .thenReturn(Optional.of(user));

        when(categoryRepository.findById(999))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskService.createTask(request)
        );

        assertEquals("Category not found", exception.getMessage());

        verify(taskRepository, never()).save(any(Task.class));

    }

    @Test
    void getTaskByIdReturnsExistingTask() {
        User user = new User("Timmy", "timmy@example.se");
        Category category = new Category("Work");

        Task task = new Task(
                "Pull docker images", "Restart docker", false, user, category
        );

        when(taskRepository.findById(1))
                .thenReturn(Optional.of(task));

        TaskResponse response = taskService.getTaskById(1);

        assertEquals("Pull docker images", response.getTitle());
        assertEquals("Restart docker", response.getDescription());
        assertFalse(response.isCompleted());
        assertEquals("Timmy", response.getUserName());
        assertEquals("Work", response.getCategoryName());

        verify(taskRepository).findById(1);

    }

    @Test
    void getTaskByIdThrowsWhenTaskDoesNotExist() {
        when(taskRepository.findById(999))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskService.getTaskById(999)
        );

        assertEquals("Task not found", exception.getMessage());
    }

}
