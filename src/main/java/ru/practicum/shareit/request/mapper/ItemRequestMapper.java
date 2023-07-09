package ru.practicum.shareit.request.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemCreatedOnRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemRequestMapper {
    private final UserService userService;

    @Autowired
    public ItemRequestMapper(UserService userService) {
        this.userService = userService;
    }

    public ItemRequest toItemRequest(ItemRequestCreationDto itemRequestCreationDto) {
        return ItemRequest.builder()
                .description(itemRequestCreationDto.getDescription())
                .requestor(userService.getUserById(itemRequestCreationDto.getUserId()))
                .build();
    }

    public static ItemCreatedOnRequestDto toItemCreatedOnRequestDto(Item item) {
        return ItemCreatedOnRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner() == null ? null : item.getOwner().getId())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() == null ? null : item.getRequest().getId())
                .build();
    }

    public static List<ItemCreatedOnRequestDto> toItemCreatedOnRequestDto(List<Item> items) {
        List<ItemCreatedOnRequestDto> itemCreatedOnRequestDtos = new ArrayList<>();
        for (Item item : items) {
            itemCreatedOnRequestDtos.add(toItemCreatedOnRequestDto(item));
        }
        return itemCreatedOnRequestDtos;
    }
}
