package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongUserException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.message.ErrorMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Integer, Item> items;
    private final Map<Integer, Map<Integer, Item>> userItems;
    private Integer id;

    public InMemoryItemRepository() {
        items = new HashMap<>();
        userItems = new HashMap<>();
        id = 1;
    }

    @Override
    public Item addItem(Item item) {
        item.setId(id);
        items.put(id, item);
        Integer ownerId = item.getOwner().getId();
        if (userItems.containsKey(ownerId)) {
            userItems.get(ownerId).put(id, item);
        } else {
            userItems.put(
                    ownerId,
                    new HashMap<>(Map.of(id, item))
            );
        }
        return getItemById(id++);
    }

    @Override
    public Item updateItem(Item item) {
        checkItemExists(item.getId());
        checkUser(item);
        updateNotNullFields(item);
        return getItemById(item.getId());
    }

    @Override
    public Item getItemById(Integer itemId) {
        checkItemExists(itemId);
        return items.get(itemId);
    }

    @Override
    public List<Item> getAllUserItems(Integer userId) {
        if (userItems.containsKey(userId)) {
            return  new ArrayList<>(userItems.get(userId).values());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Item> findItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(i -> (i.getAvailable() &&
                        (i.getName().toLowerCase().contains(text.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(text.toLowerCase()))))
                .collect(Collectors.toList());
    }

    public void checkItemExists(Integer id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException(ErrorMessage.ITEM, id);
        }
    }

    public void checkUser(Item item) {
        Integer ownerId = item.getOwner().getId();
        if (!userItems.containsKey(ownerId) || !userItems.get(ownerId).containsKey(item.getId())) {
            throw new WrongUserException(item.getId());
        }
    }

    public void updateNotNullFields(Item item) {
        Item savedItem = items.get(item.getId());
        if (item.getName() != null) {
            savedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            savedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            savedItem.setAvailable(item.getAvailable());
        }
    }
}
