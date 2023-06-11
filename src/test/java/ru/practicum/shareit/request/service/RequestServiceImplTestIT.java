package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class RequestServiceImplTestIT {
    private final RequestService requestService;
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    @Autowired
    public RequestServiceImplTestIT(RequestService requestService, RequestRepository requestRepository,
                                    UserService userService, UserRepository userRepository,
                                    ItemService itemService, ItemRepository itemRepository) {
        this.requestService = requestService;
        this.requestRepository = requestRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.itemService = itemService;
        this.itemRepository = itemRepository;
    }

    @AfterEach
    public void afterEach() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    public void getAllUserRequestsTest() {
        User user1 = userService.addUser(
                User.builder()
                        .name("name1")
                        .email("email1@gmaol.com")
                        .build()
        );

        User user2 = userService.addUser(
                User.builder()
                        .name("name2")
                        .email("email2@gmail.com")
                        .build()
        );

        User user3 = userService.addUser(
                User.builder()
                        .name("name3")
                        .email("email3@gmail.com")
                        .build()
        );

        ItemRequest request1 = requestService.addItemRequest(
                ItemRequest.builder()
                        .description("request1Description")
                        .requestor(user1)
                        .build()
        );

        ItemRequest request2 = requestService.addItemRequest(
                ItemRequest.builder()
                        .description("request2Description")
                        .requestor(user1)
                        .build()
        );

        Item item1 = itemService.addItem(
                Item.builder()
                        .name("item1Name")
                        .description("item1Description")
                        .available(true)
                        .owner(user2)
                        .request(request1)
                        .build()
        );

        Item item2 = itemService.addItem(
                Item.builder()
                        .name("item2Name")
                        .description("item2Description")
                        .available(true)
                        .owner(user3)
                        .request(request1)
                        .build()
        );

        List<ItemRequest> requests = requestService.getAllUserRequests(user1.getId());

        assertEquals(2, requests.size());
        assertEquals(2, requests.get(0).getItems().size());
        assertEquals(0, requests.get(1).getItems().size());
        assertEquals(requests.get(0).getItems(),
                ItemRequestMapper.toItemCreatedOnRequestDto(List.of(item1, item2)));
    }
}
