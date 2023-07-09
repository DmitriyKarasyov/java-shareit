package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.state.BookingState;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.WrongBookingRightsException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
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
public class BookingServiceImplItTest {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImplItTest(BookingService bookingService, BookingRepository bookingRepository,
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

    @Test
    public void getUserBookingsByAllStateTest() throws InterruptedException {
        Integer bookerId = addBookings(false);
        List<Booking> currentBookings =
                bookingService.getUserBookingsByState(bookerId, BookingState.ALL, null);
        assertEquals(5, currentBookings.size());
    }

    @Test
    public void getUserBookingsByNoStateTest() throws InterruptedException {
        Integer bookerId = addBookings(false);
        List<Booking> currentBookings =
                bookingService.getUserBookingsByState(bookerId, BookingState.DEFAULT, null);
        assertEquals(0, currentBookings.size());
    }

    @Test
    public void getUserBookingsByCurrentStateTest() throws InterruptedException {
        Integer bookerId = addBookings(false);
        List<Booking> currentBookings =
                bookingService.getUserBookingsByState(bookerId, BookingState.CURRENT, null);
        assertEquals(1, currentBookings.size());
        assertEquals("currentItem", currentBookings.get(0).getItem().getName());
    }

    @Test
    public void getUserBookingsByPastStateTest() throws InterruptedException {
        Integer bookerId = addBookings(false);
        List<Booking> currentBookings =
                bookingService.getUserBookingsByState(bookerId, BookingState.PAST, null);
        assertEquals(2, currentBookings.size());
        assertEquals("pastItem", currentBookings.get(1).getItem().getName());
        assertEquals("pastRejectedItem", currentBookings.get(0).getItem().getName());
    }

    @Test
    public void getUserBookingsByFutureStateTest() throws InterruptedException {
        Integer bookerId = addBookings(false);
        List<Booking> currentBookings =
                bookingService.getUserBookingsByState(bookerId, BookingState.FUTURE, null);
        assertEquals(2, currentBookings.size());
        assertEquals("futureItem", currentBookings.get(1).getItem().getName());
        assertEquals("futureWaitingItem", currentBookings.get(0).getItem().getName());
    }

    @Test
    public void getUserBookingsByWaitingStateTest() throws InterruptedException {
        Integer bookerId = addBookings(false);
        List<Booking> currentBookings =
                bookingService.getUserBookingsByState(bookerId, BookingState.WAITING, null);
        assertEquals(1, currentBookings.size());
        assertEquals("futureWaitingItem", currentBookings.get(0).getItem().getName());
    }

    @Test
    public void getUserBookingsByRejectedStateTest() throws InterruptedException {
        Integer bookerId = addBookings(false);
        List<Booking> currentBookings =
                bookingService.getUserBookingsByState(bookerId, BookingState.REJECTED, null);
        assertEquals(1, currentBookings.size());
        assertEquals("pastRejectedItem", currentBookings.get(0).getItem().getName());
    }

    @Test
    public void getOwnerBookingsByAllStateTest() throws InterruptedException {
        Integer bookerId = addBookings(true);
        List<Booking> currentBookings =
                bookingService.getOwnerBookingsByState(bookerId, BookingState.ALL, null);
        assertEquals(5, currentBookings.size());
    }

    @Test
    public void getOwnerBookingsByNoStateTest() throws InterruptedException {
        Integer bookerId = addBookings(true);
        List<Booking> currentBookings =
                bookingService.getOwnerBookingsByState(bookerId, BookingState.DEFAULT, null);
        assertEquals(0, currentBookings.size());
    }

    @Test
    public void getOwnerBookingsByCurrentStateTest() throws InterruptedException {
        Integer bookerId = addBookings(true);
        List<Booking> currentBookings =
                bookingService.getOwnerBookingsByState(bookerId, BookingState.CURRENT, null);
        assertEquals(1, currentBookings.size());
        assertEquals("currentItem", currentBookings.get(0).getItem().getName());
    }

    @Test
    public void getOwnerBookingsByPastStateTest() throws InterruptedException {
        Integer bookerId = addBookings(true);
        List<Booking> currentBookings =
                bookingService.getOwnerBookingsByState(bookerId, BookingState.PAST, null);
        assertEquals(2, currentBookings.size());
        assertEquals("pastItem", currentBookings.get(1).getItem().getName());
        assertEquals("pastRejectedItem", currentBookings.get(0).getItem().getName());
    }

    @Test
    public void getOwnerBookingsByFutureStateTest() throws InterruptedException {
        Integer bookerId = addBookings(true);
        List<Booking> currentBookings =
                bookingService.getOwnerBookingsByState(bookerId, BookingState.FUTURE, null);
        assertEquals(2, currentBookings.size());
        assertEquals("futureItem", currentBookings.get(1).getItem().getName());
        assertEquals("futureWaitingItem", currentBookings.get(0).getItem().getName());
    }

    @Test
    public void getOwnerBookingsByWaitingStateTest() throws InterruptedException {
        Integer bookerId = addBookings(true);
        List<Booking> currentBookings =
                bookingService.getOwnerBookingsByState(bookerId, BookingState.WAITING, null);
        assertEquals(1, currentBookings.size());
        assertEquals("futureWaitingItem", currentBookings.get(0).getItem().getName());
    }

    @Test
    public void getOwnerBookingsByRejectedStateTest() throws InterruptedException {
        Integer bookerId = addBookings(true);
        List<Booking> currentBookings =
                bookingService.getOwnerBookingsByState(bookerId, BookingState.REJECTED, null);
        assertEquals(1, currentBookings.size());
        assertEquals("pastRejectedItem", currentBookings.get(0).getItem().getName());
    }

    private User addOwner() {
        return userRepository.save(
                User.builder()
                        .name("ownerName")
                        .email("owner@gmail.com")
                        .build()
        );
    }

    private User addBooker() {
        return userRepository.save(
                User.builder()
                        .name("bookerName")
                        .email("booker@gmail.com")
                        .build()
        );
    }

    private Integer addBookings(Boolean returnOwnerId) throws InterruptedException {
        User owner = addOwner();
        User booker = addBooker();
        Item currentItem = itemRepository.save(
                Item.builder()
                        .name("currentItem")
                        .description("currentItemDescription")
                        .owner(owner)
                        .available(true)
                        .build()
        );
        Item pastItem = itemRepository.save(
                Item.builder()
                        .name("pastItem")
                        .description("pastItemDescription")
                        .owner(owner)
                        .available(true)
                        .build()
        );
        Item futureItem = itemRepository.save(
                Item.builder()
                        .name("futureItem")
                        .description("futureItemDescription")
                        .owner(owner)
                        .available(true)
                        .build()
        );
        Item futureWaitingItem = itemRepository.save(
                Item.builder()
                        .name("futureWaitingItem")
                        .description("futureWaitingItemDescription")
                        .owner(owner)
                        .available(true)
                        .build()
        );
        Item pastRejectedItem = itemRepository.save(
                Item.builder()
                        .name("pastRejectedItem")
                        .description("pastRejectedItemDescription")
                        .owner(owner)
                        .available(true)
                        .build()
        );
        bookingRepository.save(
                Booking.builder()
                        .item(pastItem)
                        .booker(booker)
                        .start(LocalDateTime.now().plus(100, ChronoUnit.MILLIS))
                        .end(LocalDateTime.now().plus(300, ChronoUnit.MILLIS))
                        .status(BookingStatus.APPROVED)
                        .build()
        );
        Thread.sleep(300);
        bookingRepository.save(
                Booking.builder()
                        .item(currentItem)
                        .booker(booker)
                        .start(LocalDateTime.now().plus(100, ChronoUnit.MILLIS))
                        .end(LocalDateTime.now().plusDays(1))
                        .status(BookingStatus.APPROVED)
                        .build()
        );
        Thread.sleep(100);
        bookingRepository.save(
                Booking.builder()
                        .item(futureItem)
                        .booker(booker)
                        .start(LocalDateTime.now().plusDays(2))
                        .end(LocalDateTime.now().plusDays(4))
                        .status(BookingStatus.APPROVED)
                        .build()
        );
        bookingRepository.save(
                Booking.builder()
                        .item(futureWaitingItem)
                        .booker(booker)
                        .start(LocalDateTime.now().plusDays(5))
                        .end(LocalDateTime.now().plusDays(6))
                        .status(BookingStatus.WAITING)
                        .build()
        );
        bookingRepository.save(
                Booking.builder()
                        .item(pastRejectedItem)
                        .booker(booker)
                        .start(LocalDateTime.now().plus(100, ChronoUnit.MILLIS))
                        .end(LocalDateTime.now().plus(300, ChronoUnit.MILLIS))
                        .status(BookingStatus.REJECTED)
                        .build()
        );
        Thread.sleep(300);
        if (returnOwnerId) {
            return owner.getId();
        } else {
            return booker.getId();
        }
    }
}
