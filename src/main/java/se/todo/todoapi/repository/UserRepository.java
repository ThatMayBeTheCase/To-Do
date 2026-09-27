package se.todo.todoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.todo.todoapi.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}
