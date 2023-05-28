package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItemById(Integer itemId);

    List<Item> getAllUserItems(Integer userId);

    List<Item> findItems(String text);
}
