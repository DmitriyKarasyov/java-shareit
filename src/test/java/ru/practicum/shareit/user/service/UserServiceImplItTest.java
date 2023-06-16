package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class UserServiceImplItTest {
    private final UserService service;
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImplItTest(UserService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @AfterEach
    public void clean() {
        userRepository.deleteAll();
    }

    @Test
    public void saveAndUpdateTest() {
        User user = User.builder()
                .name("name")
                .email("email@gmail.com")
                .build();
        User savedUser = service.addUser(user);
        assertEquals("name", savedUser.getName());
        assertEquals("email@gmail.com", savedUser.getEmail());

        assertEquals(savedUser, service.getUserById(savedUser.getId()));

        User updateUser = User.builder()
                .id(savedUser.getId())
                .name("updateName")
                .build();
        service.updateUser(updateUser);

        assertEquals("updateName", service.getUserById(savedUser.getId()).getName());
        assertEquals("email@gmail.com", service.getUserById(savedUser.getId()).getEmail());
    }
}
