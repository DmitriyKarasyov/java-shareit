package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingCreateDto {
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Integer itemId;
}
