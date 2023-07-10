package ru.practicum.shareit.exception;

import lombok.Data;

@Data
public class DuplicateEmailException extends RuntimeException {

    private final String email;

    @Override
    public String getMessage() {
        return String.format("Электронная почта %s уже добавлена.", email);
    }
}
