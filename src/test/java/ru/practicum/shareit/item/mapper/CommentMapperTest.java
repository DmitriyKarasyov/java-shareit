package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.state.BookingState;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class CommentMapperTest {
    @MockBean
    private UserService userService;

    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private CommentMapper commentMapper;

    @Test
    public void toCommentTest() {
        Integer itemId = 123;
        Integer userId = 456;
        CommentCreationDto commentCreationDto = CommentCreationDto.builder().text("what a shit").build();
        Item item = Item.builder().id(itemId).build();
        User user = User.builder().id(userId).build();
        when(itemRepository.getReferenceById(itemId)).thenReturn(item);
        when(userService.getUserById(userId)).thenReturn(user);
        Comment comment = commentMapper.toComment(commentCreationDto, userId, itemId);
        assertEquals("what a shit", comment.getText());
        assertEquals(user, comment.getAuthor());
        assertEquals(item, comment.getItem());
    }

    @Test
    public void toBookingStateTestOk() {
        assertEquals(BookingState.CURRENT, StateMapper.toBookingState("CURRENT"));
        assertEquals(BookingState.FUTURE, StateMapper.toBookingState("FUTURE"));
        assertThrows(UnknownStateException.class, () -> StateMapper.toBookingState("dasdsa"));
    }

}
