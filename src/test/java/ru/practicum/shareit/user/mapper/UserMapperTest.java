package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void userToDtoTest() {
        User user = User.builder().id(123).name("Ivan").email("ivan@ivanov.ru").build();
        UserDto dto = userMapper.toUserDto(user);
        assertEquals(123, dto.getId());
        assertEquals("Ivan", dto.getName());
        assertEquals("ivan@ivanov.ru", dto.getEmail());
    }

    @Test
    public void dtoToUserTest() {
        UserDto userDto = UserDto.builder().id(123).name("pyotr").email("petrov").build();
        User user = userMapper.toUser(userDto);
        assertEquals(123, user.getId());
        assertEquals("pyotr", user.getName());
        assertEquals("petrov", user.getEmail());
    }

    @Test
    public void toUserDtoListTest() {
        User user1 = User.builder().id(123).name("Ivan").email("ivan@ivanov.ru").build();
        User user2 = User.builder().id(456).name("Pyotr").email("pyotr@ivanov.ru").build();

        List<UserDto> dtos = userMapper.toUserDtoList(List.of(user1, user2));
        assertEquals(2, dtos.size());
        assertEquals(123, dtos.get(0).getId());
        assertEquals(456, dtos.get(1).getId());
    }
}
