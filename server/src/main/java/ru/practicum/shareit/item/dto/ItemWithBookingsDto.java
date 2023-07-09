package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingInItemDto;

import java.util.List;

@Data
@Builder
public class ItemWithBookingsDto {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
    private BookingInItemDto lastBooking;
    private BookingInItemDto nextBooking;
    private List<CommentDto> comments;
}
