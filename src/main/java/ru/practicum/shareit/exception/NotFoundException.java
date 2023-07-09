package ru.practicum.shareit.exception;

import lombok.Data;

@Data
public class NotFoundException extends RuntimeException {

    private final String objectType;
    private final Integer id;

    @Override
    public String getMessage() {
        return String.format("%s with id = %d is not found", objectType, id);
    }
}
