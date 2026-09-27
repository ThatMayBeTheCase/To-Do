package se.todo.todoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.todo.todoapi.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
