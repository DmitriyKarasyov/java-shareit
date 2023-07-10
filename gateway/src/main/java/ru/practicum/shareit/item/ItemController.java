package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> postItem(@RequestHeader(name = HEADER) Integer userId,
                                          @RequestBody @Valid ItemDto itemDto) {
        log.info("post item {}", itemDto);
        return itemClient.postItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(name = HEADER) Integer userId,
                                             @PathVariable Integer itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("patch item with id={}", itemId);
        return itemClient.patchItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(name = HEADER) Integer userId,
                                           @PathVariable Integer itemId) {
        log.info("get item with id={}", itemId);
        return itemClient.getItemWithBookings(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader(name = HEADER) Integer userId,
                                @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("get user items, userId={}, from={}, size={}", userId, from, size);
        return itemClient.getAllUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItems(@RequestHeader(name = HEADER) Integer userId,
                                   @RequestParam String text,
                                   @RequestParam(required = false, defaultValue = "0") Integer from,
                                   @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("search for item by text={}, from={}, size={}", text, from, size);
        return itemClient.findItems(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@PathVariable Integer itemId,
                                @RequestBody CommentCreationDto commentCreationDto,
                                @RequestHeader(name = HEADER) Integer authorId) {
        if (commentCreationDto.getText() == null
                || commentCreationDto.getText().isBlank()
                || commentCreationDto.getText().isEmpty()) {
            throw new IllegalArgumentException("text cannot be empty");
        }
        log.info("post comment to item with id={}, authorId={}, comment={}", itemId, authorId, commentCreationDto);
        return itemClient.postComment(itemId, authorId, commentCreationDto);
    }
}
