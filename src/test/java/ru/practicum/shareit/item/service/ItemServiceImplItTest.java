package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class ItemServiceImplItTest {
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;


    @Autowired
    public ItemServiceImplItTest(ItemService itemService, ItemRepository itemRepository,
                                 UserService userService, UserRepository userRepository,
                                 BookingService bookingService, BookingRepository bookingRepository) {
        this.itemService = itemService;
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
    }

    @AfterEach
    public void clean() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    public void getAllUserItemsTest() throws InterruptedException {
        List<Booking> addedBookings = addItemsWithBookings();
        List<ItemWithBookingsDto> user1Items = itemService.getAllUserItems(
                addedBookings.get(0).getItem().getOwner().getId(), null);

        assertEquals(3, user1Items.size());
        assertEquals(user1Items.get(0).getLastBooking().getId(), addedBookings.get(1).getId());
        assertEquals(user1Items.get(0).getNextBooking().getId(), addedBookings.get(2).getId());
        assertNull(user1Items.get(1).getLastBooking());
        assertEquals(user1Items.get(1).getNextBooking().getId(), addedBookings.get(4).getId());

        List<ItemWithBookingsDto> user2Items = itemService.getAllUserItems(
                addedBookings.get(0).getBooker().getId(), null);
        assertEquals(0, user2Items.size());
    }

    @Test
    public void getItemWithBookingsTest() throws InterruptedException {
        List<Booking> addedBookings = addItemsWithBookings();
        ItemWithBookingsDto itemWithBookings = itemService.getItemWithBookings(addedBookings.get(0).getItem().getId(),
                addedBookings.get(0).getItem().getOwner().getId());

        assertNotNull(itemWithBookings);
        assertEquals("item1Name", itemWithBookings.getName());
        assertEquals(itemWithBookings.getLastBooking().getId(), addedBookings.get(1).getId());
        assertEquals(itemWithBookings.getNextBooking().getId(), addedBookings.get(2).getId());

        assertThrows(NotFoundException.class, () -> itemService.getItemWithBookings(
                addedBookings.get(5).getItem().getId() + 2, addedBookings.get(5).getBooker().getId()));

        ItemWithBookingsDto itemWithoutBookings = itemService.getItemWithBookings(
                addedBookings.get(0).getItem().getId(), addedBookings.get(0).getBooker().getId());

        assertNotNull(itemWithoutBookings);
        assertEquals("item1Name", itemWithoutBookings.getName());
        assertNull(itemWithoutBookings.getLastBooking());
        assertNull(itemWithoutBookings.getNextBooking());
    }

    private List<Booking> addItemsWithBookings() throws InterruptedException {
        User user1 = userService.addUser(
                User.builder()
                        .name("user1")
                        .email("user1@gmail.com")
                        .build()
        );
        User user2 = userService.addUser(
                User.builder()
                        .name("user2")
                        .email("user2@gmail.com")
                        .build()
        );
        Item item1 = itemService.addItem(
                Item.builder()
                        .name("item1Name")
                        .description("item1Description")
                        .available(true)
                        .owner(user1)
                        .build()
        );
        Item item2 = itemService.addItem(
                Item.builder()
                        .name("item2Name")
                        .description("item2Description")
                        .available(true)
                        .owner(user1)
                        .build()
        );
        Item item3 = itemService.addItem(
                Item.builder()
                        .name("item3Name")
                        .description("item3Description")
                        .available(true)
                        .owner(user1)
                        .build()
        );
        Booking booking1 = bookingRepository.save(
                Booking.builder()
                        .item(item1)
                        .booker(user2)
                        .start(LocalDateTime.now().plus(20, ChronoUnit.MILLIS))
                        .end(LocalDateTime.now().plus(30, ChronoUnit.MILLIS))
                        .status(BookingStatus.WAITING)
                        .build()
        );
        Thread.sleep(30);
        Booking booking2 = bookingRepository.save(
                Booking.builder()
                        .item(item1)
                        .booker(user2)
                        .start(LocalDateTime.now().plus(10, ChronoUnit.MILLIS))
                        .end(LocalDateTime.now().plusDays(1))
                        .status(BookingStatus.WAITING)
                        .build()
        );
        Thread.sleep(10);
        Booking booking3 = bookingRepository.save(
                Booking.builder()
                        .item(item1)
                        .booker(user2)
                        .start(LocalDateTime.now().plusDays(2))
                        .end(LocalDateTime.now().plusDays(3))
                        .status(BookingStatus.WAITING)
                        .build()
        );
        Booking booking4 = bookingRepository.save(
                Booking.builder()
                        .item(item1)
                        .booker(user2)
                        .start(LocalDateTime.now().plusDays(12))
                        .end(LocalDateTime.now().plusDays(14))
                        .status(BookingStatus.WAITING)
                        .build()
        );
        Booking booking5 = bookingRepository.save(
                Booking.builder()
                        .item(item2)
                        .booker(user2)
                        .start(LocalDateTime.now().plusDays(2))
                        .end(LocalDateTime.now().plusDays(4))
                        .status(BookingStatus.WAITING)
                        .build()
        );
        Booking booking6 = bookingRepository.save(
                Booking.builder()
                        .item(item2)
                        .booker(user2)
                        .start(LocalDateTime.now().plusDays(5))
                        .end(LocalDateTime.now().plusDays(7))
                        .status(BookingStatus.WAITING)
                        .build()
        );
        return List.of(booking1, booking2, booking3, booking4, booking5, booking6);
    }
}
