package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;
    private final ItemMapper mapper;
    private static final String HEADER = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemService service, ItemMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(name = HEADER) Integer userId,
                           @RequestBody @Valid ItemDto itemDto) {
        Item item = mapper.toItem(itemDto, userId, null);
        return mapper.toItemDto(service.addItem(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(name = HEADER) Integer userId,
                              @PathVariable Integer itemId,
                              @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        Item item = mapper.toItem(itemDto, userId, null);
        return mapper.toItemDto(service.updateItem(item));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Integer itemId) {
        return mapper.toItemDto(service.getItemById(itemId));
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader(name = HEADER) Integer userId) {
        return mapper.toItemDtoList(service.getAllUserItems(userId));
    }

    @GetMapping("/search")
    public List<ItemDto> findItems(@RequestParam String text) {
        return mapper.toItemDtoList(service.findItems(text));
    }
}
