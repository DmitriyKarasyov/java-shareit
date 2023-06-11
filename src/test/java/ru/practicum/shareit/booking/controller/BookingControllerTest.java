package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.state.BookingState;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.common.RequestParser;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService bookingService;
    @MockBean
    BookingMapper bookingMapper;
    private static final String HEADER = "X-Sharer-User-Id";
    private static Item item;
    private static User booker;
    private static Booking booking;

    @BeforeAll
    public static void beforeAll() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        item = Item.builder()
                .id(1)
                .name("itemName")
                .build();
        booker = User.builder()
                .id(1)
                .name("userName")
                .build();
        booking = Booking.builder()
                .id(1)
                .item(item)
                .booker(booker)
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .build();
    }



    @Test
    public void addBookingTest() throws Exception {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .itemId(1)
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
        booking.setStatus(BookingStatus.WAITING);
        when(bookingMapper.toBooking(bookingCreateDto, 1)).thenReturn(booking);
        when(bookingService.addBooking(booking)).thenReturn(booking);

        mvc.perform(post("/bookings")
                .content(mapper.writeValueAsString(bookingCreateDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.name", is(item.getName())))
                .andExpect(jsonPath("$.booker.name", is(booker.getName())))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())));
    }

    @Test
    public void changeStatusTest() throws Exception {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingService.changeStatus(2, 1, true)).thenReturn(booking);
        mvc.perform(patch("/bookings/1?approved=true")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HEADER, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.name", is(item.getName())))
                .andExpect(jsonPath("$.booker.name", is(booker.getName())))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())));
    }

    @Test
    public void getBookingByIdTest() throws Exception {
        when(bookingService.getBookingById(1, 1)).thenReturn(booking);
        mvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.name", is(item.getName())))
                .andExpect(jsonPath("$.booker.name", is(booker.getName())));
    }

    @Test
    public void getUserBookingsByStateTest() throws Exception {
        when(bookingService.getUserBookingsByState(1, BookingState.ALL,
                RequestParser.makePageable(0, 1))).thenReturn(List.of(booking));
        mvc.perform(get("/bookings?state=ALL&from=0&size=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].item.name", is(item.getName())))
                .andExpect(jsonPath("$[0].booker.name", is(booker.getName())));
    }

    @Test
    public void getOwnerBookingsByState() throws Exception {
        when(bookingService.getOwnerBookingsByState(2, BookingState.ALL,
                RequestParser.makePageable(0, 1))).thenReturn(List.of(booking));
        mvc.perform(get("/bookings/owner?state=ALL&from=0&size=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].item.name", is(item.getName())))
                .andExpect(jsonPath("$[0].booker.name", is(booker.getName())));
    }
}
