package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService service;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService service, UserMapper userMapper) {
        this.service = service;
        this.userMapper = userMapper;
    }

    @PostMapping
    public UserDto addUser(@RequestBody UserDto userDto) {
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(service.addUser(user));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Integer userId) {
        userDto.setId(userId);
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(service.updateUser(user));
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Integer userId) {
        return userMapper.toUserDto(service.getUserById(userId));
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userMapper.toUserDtoList(service.getAllUsers());
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Integer userId) {
        service.deleteUser(userId);
    }
}
