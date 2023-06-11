package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.state.BookingState;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.SameStatusException;
import ru.practicum.shareit.exception.WrongBookingRightsException;
import ru.practicum.shareit.exception.WrongBookingTimeException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.message.ErrorMessage;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserService userService,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public Booking addBooking(Booking booking) {
        checkTime(booking);
        checkOwner(booking, booking.getBooker().getId(), false);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking changeStatus(Integer ownerId, Integer bookingId, Boolean approved) {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        checkOwner(booking,ownerId, true);
        BookingStatus status = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        if (!booking.getStatus().equals(status)) {
            booking.setStatus(status);
        } else {
            throw new SameStatusException(bookingId, status);
        }
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking getBookingById(Integer bookingId, Integer userId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException(ErrorMessage.BOOKING, bookingId);
        }
        Booking booking = bookingRepository.getReferenceById(bookingId);
        checkUserRights(booking, userId);
        return booking;
    }

    @Override
    @Transactional
    public List<Booking> getUserBookingsByState(Integer bookerId, BookingState state) {
        userService.getUserById(bookerId);
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBooker_IdOrderByStartDesc(bookerId);
            case CURRENT:
                return bookingRepository.getCurrentByBooker(bookerId);
            case PAST:
                return bookingRepository.findByBooker_IdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findByBooker_IdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED);
            default:
                return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public List<Booking> getOwnerBookingsByState(Integer ownerId, BookingState state) {
        userService.getUserById(ownerId);
        switch (state) {
            case ALL:
                return bookingRepository.findByItem_Owner_IdOrderByStartDesc(ownerId);
            case CURRENT:
                return bookingRepository.findByStartBeforeAndEndAfterAndItem_Owner_IdOrderByStartDesc(
                        LocalDateTime.now(), LocalDateTime.now(), ownerId);
            case PAST:
                return bookingRepository.findByItem_Owner_IdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findByItem_Owner_IdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
            default:
                return new ArrayList<>();
        }
    }

    public void checkTime(Booking booking) {
         if (!(booking.getStart() != null &&
                 booking.getEnd() != null &&
                 booking.getStart().isAfter(LocalDateTime.now()) &&
                 booking.getEnd().isAfter(booking.getStart()))) {
             throw new WrongBookingTimeException();
         }
    }

    public void checkOwner(Booking booking, Integer ownerId, Boolean mustBeOwner) {
        if (mustBeOwner) {
            if (!(Objects.equals(booking.getItem().getOwner().getId(), ownerId))) {
                throw new WrongBookingRightsException(ownerId);
            }
        } else {
            if ((Objects.equals(booking.getItem().getOwner().getId(), ownerId))) {
                throw new WrongBookingRightsException(ownerId);
            }
        }
    }

    public void checkUserRights(Booking booking, Integer userId) {
        if (!(Objects.equals(booking.getBooker().getId(), userId) ||
                Objects.equals(booking.getItem().getOwner().getId(), userId))) {
                throw new WrongBookingRightsException(userId);
        }
    }
}
