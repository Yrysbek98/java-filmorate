package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User change(User user);

    void addFriend(int id);

    void deleteFriend(int id);

    Collection<User> getAllFriends();

}
