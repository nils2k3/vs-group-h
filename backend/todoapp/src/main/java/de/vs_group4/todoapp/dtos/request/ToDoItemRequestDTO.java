package de.vs_group4.todoapp.dtos.request;

import java.io.Serializable;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class ToDoItemRequestDTO implements Serializable {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @Nullable
    private Boolean completed;
    
}
