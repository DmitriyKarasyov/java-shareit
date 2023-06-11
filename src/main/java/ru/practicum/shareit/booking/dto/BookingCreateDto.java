package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class BookingCreateDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer itemId;
}
