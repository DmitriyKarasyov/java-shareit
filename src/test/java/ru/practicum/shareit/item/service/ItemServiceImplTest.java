package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.WrongUserException;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

public class ItemServiceImplTest {
    private static ItemServiceImpl service;
    private static ItemRepository itemRepository;
    private static ItemMapper itemMapper;
    private static BookingRepository bookingRepository;

    @BeforeAll
    public static void beforeAll() {
        itemRepository = Mockito.mock(ItemRepository.class);
        itemMapper = Mockito.mock(ItemMapper.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        service = new ItemServiceImpl(itemRepository, itemMapper, bookingRepository, commentRepository);
    }

    @Test
    public void testUpdateItem() {
        User user1 = User.builder()
                .id(1)
                .build();
        User user2 = User.builder()
                .id(2)
                .build();
        Item item = Item.builder()
                .id(1)
                .name("name")
                .available(false)
                .description("description")
                .owner(user1)
                .build();
        Item updateItem = Item.builder()
                .id(1)
                .name("updateName")
                .description("updateDescription")
                .available(true)
                .owner(user1)
                .build();
        Item updateItemWrongOwner = Item.builder()
                .id(1)
                .name("updateName")
                .available(true)
                .owner(user2)
                .build();
        Item updateItemNoFields = Item.builder()
                .id(1)
                .owner(user1)
                .build();
        Mockito.when(itemRepository.getReferenceById(1)).thenReturn(item);
        Mockito.when(itemRepository.save(any())).thenAnswer(invocationOnMock ->  invocationOnMock.getArguments()[0]);

        assertEquals("name", service.updateItem(updateItemNoFields).getName());
        assertEquals(false, service.updateItem(updateItemNoFields).getAvailable());
        assertEquals("description", service.updateItem(updateItemNoFields).getDescription());

        assertEquals("updateName", service.updateItem(updateItem).getName());
        assertEquals(true, service.updateItem(updateItem).getAvailable());
        assertEquals("updateDescription", service.updateItem(updateItem).getDescription());

        assertThrows(WrongUserException.class,
                () -> service.updateItem(updateItemWrongOwner));
    }

    @Test
    public void testGetAllUserItems() {
        Item item1 = Item.builder()
                .id(1)
                .build();
        Item item2 = Item.builder()
                .id(2)
                .build();
        Item item3 = Item.builder()
                .id(3)
                .build();
        Booking booking1 = Booking.builder()
                .id(1)
                .item(item1)
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().minusDays(8))
                .build();
        Booking booking2 = Booking.builder()
                .id(2)
                .item(item1)
                .start(LocalDateTime.now().minusDays(4))
                .end(LocalDateTime.now().plusDays(1))
                .build();
        Booking booking3 = Booking.builder()
                .id(3)
                .item(item1)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(4))
                .build();
        Booking booking4 = Booking.builder()
                .id(4)
                .item(item1)
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(7))
                .build();
        Booking booking5 = Booking.builder()
                .id(5)
                .item(item2)
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().minusDays(5))
                .build();
        Booking booking6 = Booking.builder()
                .id(6)
                .item(item2)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();
        Mockito.when(itemRepository.findByOwnerIdOrderById(1, null))
                .thenReturn(List.of(item1, item2, item3));
        Mockito.when(bookingRepository.findByItemIn(List.of(item1, item2, item3)))
                .thenReturn(List.of(booking1, booking2, booking3, booking4, booking5, booking6));
        Mockito.when(itemMapper.toItemWithBookingsDto(item1)).thenReturn(ItemWithBookingsDto.builder().id(1).build());
        Mockito.when(itemMapper.toItemWithBookingsDto(item2)).thenReturn(ItemWithBookingsDto.builder().id(2).build());
        Mockito.when(itemMapper.toItemWithBookingsDto(item3)).thenReturn(ItemWithBookingsDto.builder().id(3).build());

        List<ItemWithBookingsDto> itemsWithBookings = service.getAllUserItems(1, null);

        assertEquals(itemsWithBookings.size(), 3);
        assertEquals(itemsWithBookings.get(0).getLastBooking(), BookingMapper.toBookingInItemDto(booking2));
        assertEquals(itemsWithBookings.get(0).getNextBooking(), BookingMapper.toBookingInItemDto(booking3));
        assertEquals(itemsWithBookings.get(1).getLastBooking(), BookingMapper.toBookingInItemDto(booking6));
        assertNull(itemsWithBookings.get(1).getNextBooking());
        assertNull(itemsWithBookings.get(2).getLastBooking());
        assertNull(itemsWithBookings.get(2).getNextBooking());
    }
}
