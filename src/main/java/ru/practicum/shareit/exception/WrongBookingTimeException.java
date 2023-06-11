package ru.practicum.shareit.exception;

public class WrongBookingTimeException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Время бронирования задано некорректно";
    }
}
