package de.vs_group4.todoapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.vs_group4.todoapp.models.ToDoItem;

@Repository
public interface ToDoRepository extends JpaRepository<ToDoItem, Long> {
}
