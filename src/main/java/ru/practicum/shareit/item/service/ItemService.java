package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItemById(Integer itemId);

    ItemWithBookingsDto getItemWithBookings(Integer itemId, Integer userId);

    List<ItemWithBookingsDto> getAllUserItems(Integer userId, Pageable pageable);

    List<Item> findItems(String text, Pageable pageable);

    Comment addComment(Comment comment);
}
