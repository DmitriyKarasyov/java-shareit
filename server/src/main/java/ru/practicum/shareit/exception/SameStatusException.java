package ru.practicum.shareit.exception;

import lombok.Data;
import ru.practicum.shareit.booking.status.BookingStatus;

@Data
public class SameStatusException extends RuntimeException {
    private final Integer bookingId;
    private final BookingStatus status;

    @Override
    public String getMessage() {
        return String.format("Booking with id = %d already has status %s", bookingId, status);
    }
}
