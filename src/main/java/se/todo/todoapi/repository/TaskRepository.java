package se.todo.todoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.todo.todoapi.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Integer> {
}
