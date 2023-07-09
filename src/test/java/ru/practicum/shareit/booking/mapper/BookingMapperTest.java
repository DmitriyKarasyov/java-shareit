package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class BookingMapperTest {
    @Autowired
    private BookingMapper bookingMapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserService userService;

    @Test
    public void toBookingTest() {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder().itemId(456)
                .start(LocalDateTime.of(2023, 6, 12, 15, 0)).end(LocalDateTime.of(2023, 6, 13, 11, 0)).build();
        User user = User.builder().id(123).build();
        Item item = Item.builder().id(456).available(true).build();
        when(itemService.getItemById(456)).thenReturn(item);
        when(userService.getUserById(123)).thenReturn(user);

        Booking booking = bookingMapper.toBooking(bookingCreateDto, 123);

        assertEquals(LocalDateTime.of(2023, 6, 12, 15, 0), booking.getStart());
        assertEquals(LocalDateTime.of(2023, 6, 13, 11, 0), booking.getEnd());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }
}
