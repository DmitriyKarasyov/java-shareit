package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WrongBookingRightsException extends RuntimeException {
    private Integer userId;

    @Override
    public String getMessage() {
        return String.format("Пользователь с id = %d не может осуществлять данные действия", userId);
    }
}
