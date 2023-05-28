package ru.practicum.shareit.item.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemMapper {

    private final UserService userService;
    private final RequestService requestService;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemMapper(UserService userService, RequestService requestService, CommentRepository commentRepository) {
        this.userService = userService;
        this.requestService = requestService;
        this.commentRepository = commentRepository;
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

    public ItemWithBookingsDto toItemWithBookingsDto(Item item) {
        return ItemWithBookingsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(null)
                .nextBooking(null)
                .comments(CommentMapper.toCommentDto(commentRepository.findByItem_IdOrderByCreatedDesc(item.getId())))
                .build();
    }

    public List<ItemWithBookingsDto> toItemWithBookingsDtoList(List<Item> itemList) {
        List<ItemWithBookingsDto> itemsWithBookingsDto = new ArrayList<>();
        for (Item item : itemList) {
            itemsWithBookingsDto.add(toItemWithBookingsDto(item));
        }
        return itemsWithBookingsDto;
    }
}
