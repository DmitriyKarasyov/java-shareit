package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestService {
    ItemRequest addItemRequest(ItemRequest itemRequest);

    List<ItemRequest> getAllUserRequests(Integer requestorId);

    List<ItemRequest> getAllRequests(Integer userId, Pageable pageable);

    ItemRequest getRequestById(Integer userId, Integer requestId);
}
