package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemCreatedOnRequestDto {
    private Integer id;
    private String name;
    private Integer ownerId;
    private String description;
    private Boolean available;
    private Integer requestId;
}
