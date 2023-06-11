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
public class UserServiceImplTestIT {
    private final UserService service;
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImplTestIT(UserService service, UserRepository userRepository) {
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
        assertEquals(1, savedUser.getId());
        assertEquals("name", savedUser.getName());
        assertEquals("email@gmail.com", savedUser.getEmail());

        assertEquals(savedUser, service.getUserById(1));

        User updateUser = User.builder()
                .id(1)
                .name("updateName")
                .build();
        service.updateUser(updateUser);

        assertEquals("updateName", service.getUserById(1).getName());
        assertEquals("email@gmail.com", service.getUserById(1).getEmail());
    }
}
