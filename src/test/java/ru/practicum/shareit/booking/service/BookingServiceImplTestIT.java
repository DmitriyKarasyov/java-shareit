package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.WrongBookingRightsException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class BookingServiceImplTestIT {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImplTestIT(BookingService bookingService, BookingRepository bookingRepository,
                                    UserService userService, UserRepository userRepository,
                                    ItemService itemService, ItemRepository itemRepository) {
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.itemService = itemService;
        this.itemRepository = itemRepository;
    }

    @AfterEach
    public void clean() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    public void changeStatusTest() {
        User owner = User.builder()
                .name("owner")
                .email("owner@gmail.com")
                .build();
        User booker = User.builder()
                .name("booker")
                .email("booker@gmail.com")
                .build();
        Item item = Item.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(userService.addUser(owner))
                .build();
        Booking booking = Booking.builder()
                .item(itemService.addItem(item))
                .booker(userService.addUser(booker))
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(4))
                .status(BookingStatus.WAITING)
                .build();
        bookingService.addBooking(booking);

        assertThrows(WrongBookingRightsException.class,
                () -> bookingService.changeStatus(booker.getId(), booking.getId(), true));
        assertEquals(BookingStatus.WAITING, bookingRepository.getReferenceById(booking.getId()).getStatus());

        bookingService.changeStatus(owner.getId(), booking.getId(), false);

        assertEquals(BookingStatus.REJECTED, bookingRepository.getReferenceById(booking.getId()).getStatus());

        bookingService.changeStatus(owner.getId(), booking.getId(), true);

        assertEquals(BookingStatus.APPROVED, bookingRepository.getReferenceById(booking.getId()).getStatus());
    }

}
