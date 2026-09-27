package se.todo.todoapi.controller;

import org.springframework.web.bind.annotation.*;
import se.todo.todoapi.dto.CreateTaskRequest;
import se.todo.todoapi.dto.TaskResponse;
import se.todo.todoapi.dto.UpdateTaskRequest;
import se.todo.todoapi.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public TaskResponse createTask(@RequestBody CreateTaskRequest request) {
        return taskService.createTask(request);
    }

    @GetMapping
    public List<TaskResponse> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(@PathVariable int id) {
        return taskService.getTaskById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable int id) {
        taskService.deleteTask(id);
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(@PathVariable int id, @RequestBody UpdateTaskRequest request) {
        return taskService.updateTask(id, request);
    }
}
