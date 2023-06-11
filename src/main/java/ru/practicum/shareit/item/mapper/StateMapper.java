package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.state.BookingState;
import ru.practicum.shareit.exception.UnknownStateException;

public class StateMapper {
    public static BookingState toBookingState(String stateString) {
        try {
            return BookingState.valueOf(stateString);
        } catch (IllegalArgumentException e) {
            throw new UnknownStateException(stateString);
        }
    }
}
