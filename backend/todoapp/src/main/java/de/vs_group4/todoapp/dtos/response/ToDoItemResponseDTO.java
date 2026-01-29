package de.vs_group4.todoapp.dtos.response;

import lombok.Value;

@Value
public class ToDoItemResponseDTO {

    private Long id;
    private String title;
    private String description;
    private Boolean completed;
}
