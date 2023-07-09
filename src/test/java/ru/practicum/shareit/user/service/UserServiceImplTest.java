package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.mockito.ArgumentMatchers.any;

public class UserServiceImplTest {
    private static UserService service;
    private static UserRepository userRepository;

    @BeforeAll
    public static void beforeAll() {
        userRepository = Mockito.mock(UserRepository.class);
        service = new UserServiceImpl(userRepository);
    }

    @Test
    public void updateUserTest() {
        User user1 = User.builder()
                .id(1)
                .name("name1")
                .email("email1")
                .build();
        User updateUser1 = User.builder()
                .id(1)
                .name("updateName1")
                .email("updateEmail1")
                .build();
        User user2 = User.builder()
                .id(2)
                .name("name2")
                .email("email2")
                .build();
        User updateUser2 = User.builder()
                .id(2)
                .email("updateEmail2")
                .build();
        Mockito.when(userRepository.save(any())).thenAnswer(invocationOnMock ->  invocationOnMock.getArguments()[0]);

    }
}
