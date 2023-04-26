package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.message.ErrorMessage;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private Map<Integer, User> users;
    private Set<String> emails;
    private Integer id;

    public InMemoryUserRepository() {
        users = new HashMap<>();
        emails = new HashSet<>();
        id = 1;
    }

    @Override
    public User addUser(User user) {
        checkEmail(user.getEmail());
        user.setId(id);
        users.put(id++, user);
        return getUserById(user.getId());
    }

    @Override
    public User updateUser(User user) {
        checkExist(user.getId());
        updateNotNullFields(user);
        return getUserById(user.getId());
    }

    @Override
    public User getUserById(Integer id) {
        checkExist(id);
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(Integer id) {
        checkExist(id);
        emails.remove(users.get(id).getEmail());
        users.remove(id);
    }

    public void checkExist(Integer id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(ErrorMessage.USER, id);
        }
    }

    public void checkEmail(String email) {
        if (emails.contains(email)) {
            throw new DuplicateEmailException(email);
        } else {
            emails.add(email);
        }
    }

    public void updateNotNullFields(User user) {
        Integer id = user.getId();
        String name = user.getName();
        String email = user.getEmail();
        User savedUser = users.get(id);
        if (name != null && email != null) {
            replaceEmail(user);
            savedUser.setName(name);
            savedUser.setEmail(email);
        } else if (name != null) {
            savedUser.setName(name);
        } else if (email != null) {
            replaceEmail(user);
            savedUser.setEmail(email);
        }
    }

    public void replaceEmail(User user) {
        Integer id = user.getId();
        String oldEmail = users.get(id).getEmail();
        String newEmail = user.getEmail();

        checkUpdatingEmail(oldEmail, newEmail);

        emails.remove(oldEmail);
        emails.add(newEmail);
    }

    public void checkUpdatingEmail(String oldEmail, String newEmail) {
        emails.remove(oldEmail);
        if (emails.contains(newEmail)) {
            emails.add(oldEmail);
            throw new DuplicateEmailException(newEmail);
        } else {
            emails.add(oldEmail);
        }
    }
}
