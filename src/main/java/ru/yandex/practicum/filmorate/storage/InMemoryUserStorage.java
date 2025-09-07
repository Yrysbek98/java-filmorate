package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        log.info("Get all users");
        return users.values();
    }

    @Override
    public User createUser(User user) {
        log.info("Create user: id={}, name={}", user.getId(), user.getName());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User changeUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователь с таким ID не найден");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.info("Change user: id={}, name={}", user.getId(), user.getName());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void addFriend(int idOfUser, int idOfFriend) {
        if (idOfUser == idOfFriend) {
            throw new UserValidationException("Нельзя добавить в друзья самого себя");
        }

        User user = users.get(idOfUser);
        if (user == null) {
            throw new UserNotFoundException("Пользователь " + idOfUser + " не найден");
        }

        User friend = users.get(idOfFriend);
        if (friend == null) {
            throw new UserNotFoundException("Пользователь " + idOfFriend + " не найден");
        }

        user.getFriends().add(idOfFriend);

        friend.getFriends().add(idOfUser);

    }

    @Override
    public void deleteFriend(int idOfUser, int idOfFriend) {
        if (idOfUser == idOfFriend) {
            throw new UserValidationException("Нельзя удалять самого себя из друзей");
        }

        User user = users.get(idOfUser);
        if (user == null) {
            throw new UserNotFoundException("Пользователь " + idOfUser + " не найден");
        }

        User friend = users.get(idOfFriend);
        if (friend == null) {
            throw new UserNotFoundException("Пользователь " + idOfFriend + " не найден");
        }
        user.getFriends().remove(idOfFriend);
        friend.getFriends().remove(idOfUser);
    }

    @Override
    public Collection<User> getSameFriends(int idOfUser, int idOfFriend) {
        if (idOfUser == idOfFriend) {
            throw new UserValidationException("Нельзя искать общих друзей у самого себя");
        }
        User user = users.get(idOfUser);
        if (user == null) {
            throw new UserNotFoundException("Пользователь " + idOfUser + " не найден");
        }
        Set<Integer> userFriends = user.getFriends();
        User friend = users.get(idOfFriend);
        if (friend == null) {
            throw new UserNotFoundException("Пользователь " + idOfFriend + " не найден");
        }
        Set<Integer> friendFriends = friend.getFriends();
        Map<Integer, User> commons = new HashMap<>();
        for (Integer num : userFriends) {
            if (friendFriends.contains(num)) {
                commons.put(num, users.get(num));
            }
        }
        return commons.values();
    }

    @Override
    public Collection<User> getFriends(int idOfUser) {
        User user = users.get(idOfUser);
        if (user == null) {
            throw new UserNotFoundException("Пользователь " + idOfUser + " не найден");
        }
        return user.getFriends().stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .toList();
    }


    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
