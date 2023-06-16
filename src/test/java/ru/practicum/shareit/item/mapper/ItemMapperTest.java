package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class ItemMapperTest {
    @MockBean
    private UserService userService;

    @MockBean
    private RequestService requestService;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private ItemMapper itemMapper;

    @Test
    public void itemToDtoTest() {
        Item item = Item.builder().id(123).available(true).description("item description").name("some item").build();
        ItemDto itemDto = itemMapper.toItemDto(item);

        assertEquals(123, itemDto.getId());
        assertTrue(itemDto.getAvailable());
        assertEquals("item description", itemDto.getDescription());
        assertEquals("some item", itemDto.getName());
        assertNull(itemDto.getRequestId());


        ItemRequest request = ItemRequest.builder().id(987).build();
        item = Item.builder().id(456).available(false).description("good item")
                .name("another item").request(request).build();
        itemDto = itemMapper.toItemDto(item);

        assertEquals(456, itemDto.getId());
        assertFalse(itemDto.getAvailable());
        assertEquals("good item", itemDto.getDescription());
        assertEquals("another item", itemDto.getName());
        assertNotNull(itemDto.getRequestId());
        assertEquals(987, itemDto.getRequestId());
    }

    @Test
    public void toItemTest() {
        Integer userId = 987;
        Integer requestId = 456;
        Integer itemId = 123;

        ItemDto itemDto = ItemDto.builder().id(itemId).available(true)
                .name("item").description("pretty item").requestId(requestId).build();
        ItemRequest itemRequest = ItemRequest.builder().id(requestId).build();
        when(requestService.getRequestById(userId, requestId)).thenReturn(itemRequest);
        User user = User.builder().id(userId).build();
        when(userService.getUserById(userId)).thenReturn(user);

        Item item = itemMapper.toItem(itemDto, userId);

        assertEquals(itemId, item.getId());
        assertTrue(item.getAvailable());
        assertEquals("item", item.getName());
        assertEquals("pretty item", item.getDescription());
        assertEquals(itemRequest, item.getRequest());
        assertEquals(user, item.getOwner());
    }

    @Test
    public void itemToDtoListTest() {
        Item firstItem = Item.builder().id(123).available(true)
                .description("item description").name("some item").build();

        ItemRequest request = ItemRequest.builder().id(987).build();
        Item secondItem = Item.builder().id(456).available(false).description("good item")
                .name("another item").request(request).build();
        List<ItemDto> dtos = itemMapper.toItemDtoList(List.of(firstItem, secondItem));

        assertEquals(2, dtos.size());

        ItemDto itemDto = dtos.get(0);
        assertEquals(123, itemDto.getId());
        assertTrue(itemDto.getAvailable());
        assertEquals("item description", itemDto.getDescription());
        assertEquals("some item", itemDto.getName());
        assertNull(itemDto.getRequestId());

        itemDto = dtos.get(1);

        assertEquals(456, itemDto.getId());
        assertFalse(itemDto.getAvailable());
        assertEquals("good item", itemDto.getDescription());
        assertEquals("another item", itemDto.getName());
        assertNotNull(itemDto.getRequestId());
        assertEquals(987, itemDto.getRequestId());
    }

    @Test
    public void toItemWithBookingsDtoTest() {
        Integer itemId = 123;
        Integer requestId = 456;
        ItemRequest itemRequest = ItemRequest.builder().id(requestId).build();
        Item item = Item.builder().id(itemId).available(true).name("item name")
                .description("awesome item").request(itemRequest).build();

        Integer comment1Id = 235;
        Integer comment2Id = 543;
        User author1 = User.builder().id(999).name("Yuda").build();
        Comment comment1 = Comment.builder().id(comment1Id).item(item).text("bad item")
                .created(LocalDateTime.of(2023, 6, 13, 17, 0, 0)).author(author1).build();

        User author2 = User.builder().id(998).name("Lyuda").build();
        Comment comment2 = Comment.builder().id(comment2Id).item(item).text("wow good")
                .created(LocalDateTime.of(2023, 6, 12, 15, 54, 0)).author(author2).build();

        when(commentRepository.findByItem_IdOrderByCreatedDesc(itemId)).thenReturn(List.of(comment1, comment2));

        ItemWithBookingsDto result = itemMapper.toItemWithBookingsDto(item);

        assertEquals(itemId, result.getId());
        assertEquals(requestId, result.getRequestId());
        assertEquals("item name", result.getName());
        assertEquals("awesome item", result.getDescription());
        assertTrue(result.getAvailable());
        assertEquals(2, result.getComments().size());
        assertEquals(comment1Id, result.getComments().get(0).getId());
        assertEquals("bad item", result.getComments().get(0).getText());
        assertEquals("Yuda", result.getComments().get(0).getAuthorName());
        assertEquals(LocalDateTime.of(2023, 6, 13, 17, 0, 0), result.getComments().get(0).getCreated());
        assertEquals(comment2Id, result.getComments().get(1).getId());
        assertEquals("wow good", result.getComments().get(1).getText());
        assertEquals("Lyuda", result.getComments().get(1).getAuthorName());
        assertEquals(LocalDateTime.of(2023, 6, 12, 15, 54, 0), result.getComments().get(1).getCreated());
    }
}
