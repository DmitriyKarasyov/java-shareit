package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private static final String HEADER = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemService service, ItemMapper itemMapper, CommentMapper commentMapper) {
        this.service = service;
        this.itemMapper = itemMapper;
        this.commentMapper = commentMapper;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(name = HEADER) Integer userId,
                           @RequestBody @Valid ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto, userId, null);
        return itemMapper.toItemDto(service.addItem(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(name = HEADER) Integer userId,
                              @PathVariable Integer itemId,
                              @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        Item item = itemMapper.toItem(itemDto, userId, null);
        return itemMapper.toItemDto(service.updateItem(item));
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto getItemById(@RequestHeader(name = HEADER) Integer userId,
                                              @PathVariable Integer itemId) {
        return service.getItemWithBookings(itemId, userId);
    }

    @GetMapping
    public List<ItemWithBookingsDto> getAllUserItems(@RequestHeader(name = HEADER) Integer userId) {
        return service.getAllUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findItems(@RequestParam String text) {
        return itemMapper.toItemDtoList(service.findItems(text));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Integer itemId,
                                 @RequestBody @Valid CommentCreationDto commentCreationDto,
                                 @RequestHeader(name = HEADER) Integer authorId) {
        return CommentMapper
                .toCommentDto(service.addComment(commentMapper.toComment(commentCreationDto, authorId, itemId)));
    }
}
