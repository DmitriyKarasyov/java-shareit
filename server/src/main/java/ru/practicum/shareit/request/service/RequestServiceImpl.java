package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.message.ErrorMessage;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, ItemRepository itemRepository,
                              UserService userService) {
        this.requestRepository = requestRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public ItemRequest addItemRequest(ItemRequest itemRequest) {
        return requestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> getAllUserRequests(Integer requestorId) {
        userService.getUserById(requestorId);
        List<ItemRequest> requests = requestRepository.findByRequestor_Id(requestorId);
        List<Item> itemsCreatedOnRequests = itemRepository.findByRequest_Requestor_Id(requestorId);
        Map<ItemRequest, List<Item>> itemsByRequests = sortItemsByRequests(itemsCreatedOnRequests);
        for (ItemRequest request : requests) {
            if (itemsByRequests.containsKey(request)) {
                request.setItems(ItemRequestMapper.toItemCreatedOnRequestDto(itemsByRequests.get(request)));
            } else {
                request.setItems(new ArrayList<>());
            }
        }
        return requests;
    }

    @Override
    public List<ItemRequest> getAllRequests(Integer userId, Pageable pageable) {
        List<ItemRequest> requests = requestRepository.findAllByRequestor_IdNotOrderByCreatedDesc(userId, pageable);
        List<Item> itemsCreatedOnRequests = itemRepository.findByRequestNotNull();
        Map<ItemRequest, List<Item>> itemsByRequests = sortItemsByRequests(itemsCreatedOnRequests);
        for (ItemRequest request : requests) {
            if (itemsByRequests.containsKey(request)) {
                request.setItems(ItemRequestMapper.toItemCreatedOnRequestDto(itemsByRequests.get(request)));
            } else {
                request.setItems(new ArrayList<>());
            }
        }
        return requests;
    }

    @Override
    public ItemRequest getRequestById(Integer userId, Integer requestId) {
        userService.getUserById(userId);
        if (!requestRepository.existsById(requestId)) {
            throw new NotFoundException(ErrorMessage.REQUEST, requestId);
        }
        ItemRequest request = requestRepository.getReferenceById(requestId);
        request.setItems(ItemRequestMapper.toItemCreatedOnRequestDto(itemRepository.findByRequest_Id(requestId)));
        return request;
    }

    public Map<ItemRequest, List<Item>> sortItemsByRequests(List<Item> itemsCreatedOnRequests) {
        Map<ItemRequest, List<Item>> itemsByRequests = new HashMap<>();
        for (Item item : itemsCreatedOnRequests) {
            if (!itemsByRequests.containsKey(item.getRequest())) {
                itemsByRequests.put(item.getRequest(), new ArrayList<>(List.of(item)));
            } else {
                itemsByRequests.get(item.getRequest()).add(item);
            }
        }
        return itemsByRequests;
    }
}
