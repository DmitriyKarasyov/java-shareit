package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.common.RequestParser;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private ItemMapper itemMapper;

    @MockBean
    private CommentMapper commentMapper;

    private static final String HEADER = "X-Sharer-User-Id";

    private final Item item = Item.builder()
            .id(1)
            .name("itemName")
            .description("itemDescription")
            .available(true)
            .owner(User.builder()
                    .id(1)
                    .name("userName")
                    .email("email@gmail.com")
                    .build())
            .build();

    private final ItemDto itemDto = ItemDto.builder()
            .id(1)
            .name("itemName")
            .description("itemDescription")
            .available(true)
            .build();

    private final ItemWithBookingsDto itemWithBookingsDto = ItemWithBookingsDto.builder()
            .id(1)
            .name("itemName")
            .description("itemDescription")
            .available(true)
            .build();

    @BeforeEach
    public void beforeAll() {
        when(itemMapper.toItem(itemDto, 1)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);
        when(itemMapper.toItemDtoList(List.of(item))).thenReturn(List.of(itemDto));
    }

    @Test
    public void addItemTest() throws  Exception {
        when(itemService.addItem(item)).thenReturn(item);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    public void updateItemTest() throws Exception {
        when(itemService.updateItem(item)).thenReturn(item);
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    public void getItemByIdTest() throws Exception {
        when(itemService.getItemWithBookings(1, 1)).thenReturn(itemWithBookingsDto);
        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemWithBookingsDto.getName())))
                .andExpect(jsonPath("$.description", is(itemWithBookingsDto.getDescription())));
    }

    @Test
    public void getAllUserItemsTest() throws Exception {
        when(itemService.getAllUserItems(1, RequestParser.makePageable(0, 1)))
                .thenReturn(List.of(itemWithBookingsDto));
        mvc.perform(get("/items?from=0&size=1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(itemWithBookingsDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemWithBookingsDto.getDescription())));
    }

    @Test
    public void findItemsTest() throws Exception {
        when(itemService.findItems("text", RequestParser.makePageable(0, 1)))
                .thenReturn(List.of(item));
        mvc.perform(get("/items/search?text=text&from=0&size=1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())));
    }

    @Test
    public void addCommentTest() throws Exception {
        CommentCreationDto commentCreationDto = CommentCreationDto.builder()
                .text("commentText")
                .build();
        Comment comment = Comment.builder()
                .id(1)
                .text("commentText")
                .author(User.builder()
                        .id(1)
                        .name("userName")
                        .build())
                .item(item)
                .created(LocalDateTime.now())
                .build();
        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .text("commentText")
                .authorName("userName")
                .created(LocalDateTime.now())
                .build();
        when(commentMapper.toComment(commentCreationDto, 1, 1)).thenReturn(comment);
        when(itemService.addComment(comment)).thenReturn(comment);
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentCreationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }
}
