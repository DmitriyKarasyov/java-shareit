package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.message.ErrorMessage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public User addUser(User user) {
        return repository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        User savedUser = repository.getReferenceById(user.getId());
        if (user.getName() != null) {
            savedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            savedUser.setEmail(user.getEmail());
        }
        return repository.save(savedUser);
    }

    @Override
    @Transactional
    public User getUserById(Integer id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException(ErrorMessage.USER, id);
        }
        return repository.getReferenceById(id);
    }

    @Override
    @Transactional
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        repository.deleteById(id);
    }
}
