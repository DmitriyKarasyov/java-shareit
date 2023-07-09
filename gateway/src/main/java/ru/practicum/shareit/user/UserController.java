package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;
    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Valid UserDto userDto) {
        log.info("post user {}", userDto);
        return userClient.postUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @PathVariable Integer userId) {
        log.info("update user with id={}", userId);
        return userClient.patchUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Integer userId) {
        log.info("get user with id={}", userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("get all users");
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Integer userId) {
        log.info("delete user with id={}", userId);
        return userClient.deleteUser(userId);
    }
}
