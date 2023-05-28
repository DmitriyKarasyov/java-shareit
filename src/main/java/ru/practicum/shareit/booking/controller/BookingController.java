package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.mapper.StateMapper;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String HEADER = "X-Sharer-User-Id";
    private final BookingService service;
    private final BookingMapper mapper;

    @Autowired
    public BookingController(BookingService service, BookingMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public Booking addBooking(@RequestHeader(name = HEADER) Integer userId,
                                       @RequestBody @Valid BookingCreateDto bookingCreateDto) {
        return service.addBooking(mapper.toBooking(bookingCreateDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public Booking changeStatus(@RequestHeader(name = HEADER) Integer ownerId,
                                @PathVariable Integer bookingId,
                                @RequestParam Boolean approved) {
        return service.changeStatus(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingById(@RequestHeader(name = HEADER) Integer userId,
                                  @PathVariable Integer bookingId) {
        return service.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<Booking> getUserBookingsByState(@RequestHeader(name = HEADER) Integer bookerId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        return service.getUserBookingsByState(bookerId, StateMapper.toBookingState(state));
    }

    @GetMapping("/owner")
    public List<Booking> getOwnerBookingsByState(@RequestHeader(name = HEADER) Integer ownerId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        return service.getOwnerBookingsByState(ownerId, StateMapper.toBookingState(state));
    }
}