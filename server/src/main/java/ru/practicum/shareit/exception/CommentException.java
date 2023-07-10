package ru.practicum.shareit.exception;

import lombok.Data;

@Data
public class CommentException extends RuntimeException {
    private final String message;

    @Override
    public String getMessage() {
        return message;
    }
}
