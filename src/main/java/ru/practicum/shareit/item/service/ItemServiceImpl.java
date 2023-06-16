package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.CommentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongUserException;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.message.ErrorMessage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           ItemMapper itemMapper,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public Item addItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(Item item) {
        Item savedItem = itemRepository.getReferenceById(item.getId());
        if (!Objects.equals(savedItem.getOwner().getId(), item.getOwner().getId())) {
            throw new WrongUserException(item.getOwner().getId());
        }
        if (item.getName() != null) {
            savedItem.setName(item.getName());
        }
        if (item.getAvailable() != null) {
            savedItem.setAvailable(item.getAvailable());
        }
        if (item.getDescription() != null) {
            savedItem.setDescription(item.getDescription());
        }
        return itemRepository.save(savedItem);
    }

    @Override
    @Transactional
    public Item getItemById(Integer itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException(ErrorMessage.ITEM, itemId);
        }
        return itemRepository.getReferenceById(itemId);
    }

    @Override
    @Transactional
    public ItemWithBookingsDto getItemWithBookings(Integer itemId, Integer userId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException(ErrorMessage.ITEM, itemId);
        }
        Item item = itemRepository.getReferenceById(itemId);
        ItemWithBookingsDto itemWithBookingsDto =
                itemMapper.toItemWithBookingsDto(item);
        if (Objects.equals(item.getOwner().getId(), userId)) {
            addBookingsToItem(itemWithBookingsDto);
        }
        return itemWithBookingsDto;
    }


    @Override
    @Transactional
    public List<ItemWithBookingsDto> getAllUserItems(Integer userId, Pageable pageable) {
        List<ItemWithBookingsDto> itemWithBookingsDtoList = new ArrayList<>();
        List<Item> items = itemRepository.findByOwnerIdOrderById(userId, pageable);
        List<Booking> userBookings = bookingRepository.findByItemIn(items);
        Map<Item, List<Booking>> bookingsByItems = sortBookings(userBookings);
        for (Item item : items) {
            ItemWithBookingsDto itemWithBookingsDto = itemMapper.toItemWithBookingsDto(item);
            if (bookingsByItems.containsKey(item)) {
                itemWithBookingsDto.setLastBooking(getLastBooking(bookingsByItems.get(item)));
                itemWithBookingsDto.setNextBooking(getNextBooking(bookingsByItems.get(item)));
            }
            itemWithBookingsDtoList.add(itemWithBookingsDto);
        }
        return itemWithBookingsDtoList;
    }

    @Override
    @Transactional
    public List<Item> findItems(String text, Pageable pageable) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text, pageable);
    }

    @Override
    @Transactional
    public Comment addComment(Comment comment) {
        checkCommentatorRentedItem(comment.getAuthor().getId(), comment.getItem().getId());
        checkCommentText(comment);
        return commentRepository.save(comment);
    }

    public void checkCommentatorRentedItem(Integer authorId, Integer itemId) {
        if (!bookingRepository.existsByBooker_IdAndItem_IdAndStartBefore(authorId, itemId, LocalDateTime.now())) {
            throw new CommentException(String.format("User %d did not rent item %d", authorId, itemId));
        }
    }

    public void checkCommentText(Comment comment) {
        if (comment.getText() == null || comment.getText().isBlank() || comment.getText().isEmpty()) {
            throw new CommentException("Text cannot be empty");
        }
    }

    public void addBookingsToItem(ItemWithBookingsDto itemWithBookingsDto) {
        Integer itemId = itemWithBookingsDto.getId();
        List<BookingStatus> suitableStatuses = List.of(BookingStatus.APPROVED, BookingStatus.WAITING);
        Optional<Booking> lastBooking = bookingRepository.findFirstByItem_IdAndStartBeforeAndStatusInOrderByStartDesc(
                itemId,
                LocalDateTime.now(),
                suitableStatuses);
        lastBooking.ifPresent(b -> itemWithBookingsDto.setLastBooking(BookingMapper.toBookingInItemDto(b)));

        Optional<Booking> nextBooking = bookingRepository.findFirstByItem_IdAndStartAfterAndStatusInOrderByStartAsc(
                itemId,
                LocalDateTime.now(),
                suitableStatuses);
        nextBooking.ifPresent(b -> itemWithBookingsDto.setNextBooking(BookingMapper.toBookingInItemDto(b)));
    }

    public Map<Item, List<Booking>> sortBookings(List<Booking> userBookings) {
        Map<Item, List<Booking>> bookingsByItems = new HashMap<>();
        for (Booking booking : userBookings) {
            if (!bookingsByItems.containsKey(booking.getItem())) {
                bookingsByItems.put(booking.getItem(), new ArrayList<>(List.of(booking)));
            } else {
                bookingsByItems.get(booking.getItem()).add(booking);
            }
        }
        return bookingsByItems;
    }

    public BookingInItemDto getLastBooking(List<Booking> bookings) {
        List<Booking> pastBookings = bookings.stream()
                .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
        if (pastBookings.isEmpty()) {
            return null;
        } else {
            return BookingMapper.toBookingInItemDto(pastBookings.get(0));
        }
    }

    public BookingInItemDto getNextBooking(List<Booking> bookings) {
        List<Booking> futureBookings = bookings.stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Booking::getStart))
                .collect(Collectors.toList());
        if (futureBookings.isEmpty()) {
            return null;
        } else {
            return BookingMapper.toBookingInItemDto(futureBookings.get(0));
        }
    }
}
