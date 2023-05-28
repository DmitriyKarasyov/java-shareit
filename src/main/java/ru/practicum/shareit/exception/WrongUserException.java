package ru.practicum.shareit.exception;

import lombok.Data;

@Data
public class WrongUserException extends RuntimeException {
    private final Integer wrongOwnerId;

    @Override
    public String getMessage() {
        return String.format("Данная вещь не размещалась пользователем с id = %d", wrongOwnerId);
    }
}
