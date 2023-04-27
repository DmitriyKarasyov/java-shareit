package ru.practicum.shareit.item.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemMapper {

    private final UserService userService;
    private final RequestService requestService;

    @Autowired
    public ItemMapper(UserService userService, RequestService requestService) {
        this.userService = userService;
        this.requestService = requestService;
    }

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public Item toItem(ItemDto itemDto, Integer userId, Integer requestId) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                userService.getUserById(userId),
                requestId != null ? requestService.getRequestById(requestId) : null
        );
    }

    public List<ItemDto> toItemDtoList(List<Item> itemList) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemList) {
            itemDtoList.add(toItemDto(item));
        }
        return itemDtoList;
    }
}
