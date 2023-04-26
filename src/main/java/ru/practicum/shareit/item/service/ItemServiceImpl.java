package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;

    public ItemServiceImpl(ItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public Item addItem(Item item) {
        return repository.addItem(item);
    }

    @Override
    public Item updateItem(Item item) {
        return repository.updateItem(item);
    }

    @Override
    public Item getItemById(Integer itemId) {
        return repository.getItemById(itemId);
    }

    @Override
    public List<Item> getAllUserItems(Integer userId) {
        return repository.getAllUserItems(userId);
    }

    @Override
    public List<Item> findItems(String text) {
        return repository.findItems(text);
    }
}
