package ru.practicum.shareit.booking.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingInItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.UnavailableItemException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Service
public class BookingMapper {
    private ItemService itemService;
    private UserService userService;

    @Autowired
    public BookingMapper(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    public Booking toBooking(BookingCreateDto bookingCreateDto, Integer userId) {
        Item item = itemService.getItemById(bookingCreateDto.getItemId());
        if (!item.getAvailable()) {
            throw new UnavailableItemException(item.getId());
        }
        User booker = userService.getUserById(userId);
        return Booking.builder()
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    public static BookingInItemDto toBookingInItemDto(Booking booking) {
        return new BookingInItemDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker() == null ? null : booking.getBooker().getId());
    }
}
