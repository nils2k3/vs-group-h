package de.vs_group4.todoapp.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.vs_group4.todoapp.dtos.request.ToDoItemRequestDTO;
import de.vs_group4.todoapp.dtos.response.ToDoItemResponseDTO;
import de.vs_group4.todoapp.services.ToDoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
@CrossOrigin(origins = "${ALLOWED_ORIGIN}")
public class ToDoController {

    private final ToDoService toDoService;

    @GetMapping
    public ResponseEntity<List<ToDoItemResponseDTO>> getAllTodos() {
        return ResponseEntity.ok(toDoService.findAllToDoItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToDoItemResponseDTO> getTodo(@PathVariable Long id) {
        return ResponseEntity.ok(toDoService.findToDoItemById(id).orElseThrow());
    }

    @PostMapping
    public ResponseEntity<ToDoItemResponseDTO> createTodo(@RequestBody @Valid ToDoItemRequestDTO newTodo) {
        return toDoService.createToDoItem(newTodo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ToDoItemResponseDTO> updateTodo(@PathVariable Long id,
            @RequestBody ToDoItemRequestDTO todoDetails) {

        return toDoService.updateToDoItem(id, todoDetails);

   }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {   
        toDoService.deleteToDoItem(id);
        return ResponseEntity.noContent().build();  
    }
    
}
