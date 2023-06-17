package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.common.RequestParser;
import ru.practicum.shareit.request.dto.ItemCreatedOnRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    private static final String HEADER = "X-Sharer-User-Id";
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemRequestMapper requestMapper;
    @MockBean
    private RequestService requestService;
    private static ItemRequest request;
    private static User requestor;
    private static ItemCreatedOnRequestDto item;

    @BeforeAll
    public static void beforeAll() {
        requestor = User.builder()
                .id(1)
                .name("requestorName")
                .build();
        item = ItemCreatedOnRequestDto.builder()
                .requestId(1)
                .id(1)
                .name("itemName")
                .build();
        request = ItemRequest.builder()
                .id(1)
                .description("requestDescription")
                .created(LocalDateTime.now())
                .requestor(requestor)
                .items(List.of(item))
                .build();

    }

    @Test
    public void addItemRequestTest() throws Exception {
        ItemRequestCreationDto requestCreationDto = ItemRequestCreationDto.builder()
                .description("requestDescription")
                .userId(1)
                .build();
        when(requestMapper.toItemRequest(requestCreationDto)).thenReturn(request);
        when(requestService.addItemRequest(request)).thenReturn(request);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestCreationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestor.name", is(requestor.getName())))
                .andExpect(jsonPath("$.items[0].name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(request.getDescription())));
    }

    @Test
    public void getAllUserRequestsTest() throws Exception {
        when(requestService.getAllUserRequests(1)).thenReturn(List.of(request));

        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestor.name", is(requestor.getName())))
                .andExpect(jsonPath("$[0].items[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description", is(request.getDescription())));
    }

    @Test
    public void getAllRequestsTest() throws Exception {
        when(requestService.getAllRequests(2, RequestParser.makePageable(0, 1)))
                .thenReturn(List.of(request));

        mvc.perform(get("/requests/all?from=0&size=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestor.name", is(requestor.getName())))
                .andExpect(jsonPath("$[0].items[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description", is(request.getDescription())));
    }

    @Test
    public void getRequestByIdTest() throws Exception {
        when(requestService.getRequestById(1, 1)).thenReturn(request);

        mvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestor.name", is(requestor.getName())))
                .andExpect(jsonPath("$.items[0].name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(request.getDescription())));
    }
}
