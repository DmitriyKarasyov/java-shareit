package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;
	private static final String HEADER = "X-Sharer-User-Id";

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader(HEADER) Integer userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnerBookings(@RequestHeader(HEADER) Integer ownerId,
											  @RequestParam(name = "state", defaultValue = "all") String stateParam,
											  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
											  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get owner booking with state {}, ownerId={}, from={}, size={}", stateParam, ownerId, from, size);
		return bookingClient.getOwnerBookings(ownerId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader(HEADER) Integer userId,
			@RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> changeStatus(@RequestHeader(name = HEADER) Integer ownerId,
								@PathVariable Integer bookingId,
								@RequestParam Boolean approved) {
		log.info("Patching booking, bookingId={}, userId={}, approved={}", bookingId, ownerId, approved);
		return bookingClient.patchBooking(ownerId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
			@PathVariable Integer bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}
}
