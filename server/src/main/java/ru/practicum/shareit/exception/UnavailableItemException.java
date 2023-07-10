package ru.practicum.shareit.exception;

import lombok.Data;

@Data
public class UnavailableItemException extends RuntimeException {
    private final Integer unavailableItemId;

    @Override
    public String getMessage() {
        return String.format("Вещь с id = %d", unavailableItemId);
    }
}
