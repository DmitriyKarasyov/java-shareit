package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private final Integer id;
    private final String text;
    private final String authorName;
    private final LocalDateTime created;
}
