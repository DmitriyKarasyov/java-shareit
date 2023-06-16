package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.SameStatusException;
import ru.practicum.shareit.exception.WrongBookingRightsException;
import ru.practicum.shareit.exception.WrongBookingTimeException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

public class BookingServiceImplTest {

    private static BookingService service;
    private static BookingRepository bookingRepository;

    @BeforeAll
    public static void beforeAll() {
        bookingRepository = Mockito.mock(BookingRepository.class);
        UserService userService = Mockito.mock(UserService.class);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        service = new BookingServiceImpl(bookingRepository,userService, itemRepository);
        Mockito.when(bookingRepository.save(any())).thenAnswer(invocationOnMock ->  invocationOnMock.getArguments()[0]);
    }

    @Test
    public void testAddBooking() {
        User user1 = User.builder()
                .id(1)
                .build();
        User user2 = User.builder()
                .id(2)
                .build();
        Item item1 = Item.builder()
                .id(1)
                .name("testName")
                .owner(user1)
                .build();
        Booking wrongStartBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(1L))
                .end(LocalDateTime.now())
                .booker(user2)
                .item(item1)
                .build();
        Booking wrongEndBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().minusDays(1))
                .booker(user2)
                .item(item1)
                .build();
        Booking noStartBooking = Booking.builder()
                .end(LocalDateTime.now().plusDays(1))
                .booker(user2)
                .item(item1)
                .build();
        Booking noEndBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .booker(user2)
                .item(item1)
                .build();

        assertThrows(WrongBookingTimeException.class, () -> service.addBooking(wrongStartBooking));
        assertThrows(WrongBookingTimeException.class, () -> service.addBooking(wrongEndBooking));
        assertThrows(WrongBookingTimeException.class, () -> service.addBooking(noStartBooking));
        assertThrows(WrongBookingTimeException.class, () -> service.addBooking(noEndBooking));

        Booking ownerBookingFail = Booking.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(4))
                .item(item1)
                .booker(user1)
                .build();

        assertThrows(WrongBookingRightsException.class, () -> service.addBooking(ownerBookingFail));

        Booking validBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(2L))
                .end(LocalDateTime.now().plusDays(4L))
                .item(item1)
                .booker(user2)
                .build();

        assertEquals(validBooking, service.addBooking(validBooking));
    }

    @Test
    public void testChangeStatus() {
        User user = User.builder()
                .id(1)
                .build();
        Item item = Item.builder()
                .id(1)
                .owner(user)
                .build();
        Booking waitingBooking1 = Booking.builder()
                .id(1)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        Booking approvedBooking1 = Booking.builder()
                .id(1)
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();
        Booking approvedBooking2 = Booking.builder()
                .id(2)
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();
        Mockito.when(bookingRepository.getReferenceById(1)).thenReturn(waitingBooking1);
        Mockito.when(bookingRepository.existsById(1)).thenReturn(true);
        Mockito.when(bookingRepository.getReferenceById(2)).thenReturn(approvedBooking2);
        Mockito.when(bookingRepository.existsById(2)).thenReturn(true);
        Mockito.when(bookingRepository.existsById(3)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.changeStatus(1, 3, true));
        assertThrows(WrongBookingRightsException.class,
                () -> service.changeStatus(2, 1, true));

        assertThrows(SameStatusException.class,
                () -> service.changeStatus(1, 2, true));

        assertEquals(approvedBooking1, service.changeStatus(1, 1, true));
    }

    @Test
    public void getBookingByIdTest() {
        User booker = User.builder()
                .id(1)
                .build();
        User owner = User.builder()
                .id(2)
                .build();
        Item item = Item.builder()
                .id(1)
                .owner(owner)
                .build();
        Booking booking = Booking.builder()
                .id(1)
                .item(item)
                .booker(booker)
                .build();
        when(bookingRepository.existsById(1)).thenReturn(true);
        when(bookingRepository.existsById(2)).thenReturn(false);
        when(bookingRepository.getReferenceById(1)).thenReturn(booking);

        assertEquals(booking, service.getBookingById(1, 1));
        assertEquals(booking, service.getBookingById(1, 2));
        assertThrows(WrongBookingRightsException.class, () -> service.getBookingById(1, 3));
        assertThrows(NotFoundException.class, () -> service.getBookingById(2, 1));
    }

}
