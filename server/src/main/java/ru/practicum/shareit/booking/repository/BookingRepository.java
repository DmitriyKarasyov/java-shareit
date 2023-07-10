package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBooker_IdOrderByStartDesc(Integer bookerId, Pageable pageable);

    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(Integer bookerId, BookingStatus status, Pageable pageable);

    @Query(" select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start <= now()" +
            "and b.end >= now()")
    List<Booking> getCurrentByBooker(Integer bookerId, Pageable pageable);

    List<Booking> findByBooker_IdAndStartAfterOrderByStartDesc(Integer bookerId, LocalDateTime dateTime,
                                                               Pageable pageable);

    List<Booking> findByBooker_IdAndEndBeforeOrderByStartDesc(Integer bookerId, LocalDateTime dateTime,
                                                              Pageable pageable);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(Integer ownerId, Pageable pageable);

    List<Booking> findByStartBeforeAndEndAfterAndItem_Owner_IdOrderByStartDesc(LocalDateTime dateTimeStart,
                                                                               LocalDateTime dateTimeEnd,
                                                                               Integer ownerId,
                                                                               Pageable pageable);

    List<Booking> findByItem_Owner_IdAndEndBeforeOrderByStartDesc(Integer ownerId, LocalDateTime dateTime,
                                                                  Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStartAfterOrderByStartDesc(Integer ownerId, LocalDateTime dateTime,
                                                                   Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(Integer ownerId, BookingStatus status,
                                                               Pageable pageable);

    Optional<Booking> findFirstByItem_IdAndStartBeforeAndStatusInOrderByStartDesc(Integer itemId,
                                                                                  LocalDateTime dateTime,
                                                                                  List<BookingStatus> suitableStatuses);

    Optional<Booking> findFirstByItem_IdAndStartAfterAndStatusInOrderByStartAsc(Integer itemId,
                                                                                LocalDateTime dateTime,
                                                                                List<BookingStatus> suitableStatuses);

    Boolean existsByBooker_IdAndItem_IdAndStartBefore(Integer bookerId, Integer itemId, LocalDateTime dateTime);

    List<Booking> findByItemIn(List<Item> items);
}
