package de.vs_group4.todoapp.mappers.request;

import org.mapstruct.Mapper;

import de.vs_group4.todoapp.dtos.request.ToDoItemRequestDTO;
import de.vs_group4.todoapp.dtos.response.ToDoItemResponseDTO;
import de.vs_group4.todoapp.mappers.EntityMapper;
import de.vs_group4.todoapp.models.ToDoItem;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface ToDoItemRequestMapper extends EntityMapper<ToDoItem, ToDoItemRequestDTO> {

    ToDoItem toEntity(ToDoItemRequestDTO dto);

    ToDoItemResponseDTO toDto(ToDoItem entity);
    
}
