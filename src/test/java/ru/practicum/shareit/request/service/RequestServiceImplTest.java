package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

public class RequestServiceImplTest {
    private static RequestService service;
    private static RequestRepository requestRepository;
    private static ItemRepository itemRepository;
    private static UserService userService;

    @BeforeAll
    public static void beforeAll() {
        requestRepository = Mockito.mock(RequestRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        userService = Mockito.mock(UserService.class);
        service = new RequestServiceImpl(requestRepository, itemRepository, userService);
    }

    @Test
    public void getAllUserRequestsTest() {
        ItemRequest request1 = ItemRequest.builder()
                .id(1)
                .build();
        ItemRequest request2 = ItemRequest.builder()
                .id(2)
                .build();
        ItemRequest request3 = ItemRequest.builder()
                .id(3)
                .build();
        Item item1 = Item.builder()
                .id(1)
                .request(request1)
                .build();
        Item item2 = Item.builder()
                .id(2)
                .request(request1)
                .build();
        Item item3 = Item.builder()
                .id(3)
                .request(request2)
                .build();
        Mockito.when(requestRepository.findByRequestor_Id(1))
                .thenReturn(List.of(request1, request2, request3));
        Mockito.when(itemRepository.findByRequest_Requestor_Id(1))
                .thenReturn(List.of(item1, item2, item3));
        List<ItemRequest> requests = service.getAllUserRequests(1);

        assertEquals(requests.size(), 3);
        assertEquals(requests.get(0).getItems().get(0), ItemRequestMapper.toItemCreatedOnRequestDto(item1));
        assertEquals(requests.get(0).getItems().get(1), ItemRequestMapper.toItemCreatedOnRequestDto(item2));
        assertEquals(requests.get(1).getItems().get(0), ItemRequestMapper.toItemCreatedOnRequestDto(item3));
        assertEquals(requests.get(2).getItems().size(), 0);
    }
}
