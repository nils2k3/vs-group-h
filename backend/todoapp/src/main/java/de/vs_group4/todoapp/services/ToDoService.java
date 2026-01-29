package de.vs_group4.todoapp.services;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import de.vs_group4.todoapp.dtos.request.ToDoItemRequestDTO;
import de.vs_group4.todoapp.dtos.response.ToDoItemResponseDTO;
import de.vs_group4.todoapp.mappers.request.ToDoItemRequestMapper;
import de.vs_group4.todoapp.models.ToDoItem;
import de.vs_group4.todoapp.repositories.ToDoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ToDoService {

    private final ToDoRepository toDoRepository;
    private final ToDoItemRequestMapper toDoItemRequestMapper;

    public ResponseEntity<ToDoItemResponseDTO> createToDoItem(ToDoItemRequestDTO newTodo) {
        ToDoItem toDoItem = toDoItemRequestMapper.toEntity(newTodo);
        toDoRepository.save(toDoItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDoItemRequestMapper.toDto(toDoItem));
    }

    public ResponseEntity<ToDoItemResponseDTO> updateToDoItem(Long id, ToDoItemRequestDTO todoDetails) {
        ToDoItem existingItem = toDoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ToDo item not found"));

        existingItem.setTitle(todoDetails.getTitle());
        existingItem.setDescription(todoDetails.getDescription());
        existingItem.setCompleted(todoDetails.getCompleted());

        toDoRepository.save(existingItem);

        ToDoItemResponseDTO responseDTO = toDoItemRequestMapper.toDto(existingItem);
        return ResponseEntity.ok(responseDTO);
    }

    public void deleteToDoItem(Long id) {
        toDoRepository.deleteById(id);
    }

    public Optional<ToDoItemResponseDTO> findToDoItemById(Long id) {
        return toDoRepository.findById(id)
                .map(toDoItemRequestMapper::toDto);
    }

    public List<ToDoItemResponseDTO> findAllToDoItems() {
        return toDoRepository.findAll().stream()
                .map(toDoItemRequestMapper::toDto)
                .toList();
    }
}