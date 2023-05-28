package ru.practicum.shareit.exception;

import lombok.Data;

@Data
public class UnknownStateException extends RuntimeException {
    private final String unknownState;

    @Override
    public String getMessage() {
        return String.format("Unknown state: %s", unknownState);
    }
}
