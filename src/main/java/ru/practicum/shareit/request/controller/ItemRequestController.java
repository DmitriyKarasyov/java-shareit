package ru.practicum.shareit.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.RequestParser;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestMapper mapper;
    private final RequestService requestService;
    private static final String HEADER = "X-Sharer-User-Id";

    @Autowired
    public ItemRequestController(ItemRequestMapper mapper, RequestService requestService) {
        this.mapper = mapper;
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequest addItemRequest(@RequestHeader(name = HEADER) Integer userId,
                                      @RequestBody @Valid ItemRequestCreationDto itemRequestCreationDto) {
        itemRequestCreationDto.setUserId(userId);
        return requestService.addItemRequest(mapper.toItemRequest(itemRequestCreationDto));
    }

    @GetMapping
    public List<ItemRequest> getAllUserRequests(@RequestHeader(name = HEADER) Integer requestorId) {
        return requestService.getAllUserRequests(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequest> getAllRequests(@RequestHeader(name = HEADER) Integer userId,
                                            @RequestParam(required = false) Integer from,
                                            @RequestParam(required = false) Integer size) {
        return requestService.getAllRequests(userId, RequestParser.makePageable(from, size));
    }

    @GetMapping("/{requestId}")
    public ItemRequest getRequestById(@RequestHeader(name = HEADER) Integer userId,
                                      @PathVariable Integer requestId) {
        return requestService.getRequestById(userId, requestId);
    }
}
