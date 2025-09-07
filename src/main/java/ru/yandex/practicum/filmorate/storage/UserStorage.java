package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();

    User createUser(User user);

    User changeUser(User user);

    void addFriend(int idOfUser, int idOfFriend);

    void deleteFriend(int idOfUser, int idOfFriend);

    Collection<User> getSameFriends(int idOfUser, int idOfFriend);

    Collection<User> getFriends(int idOfUser);

    boolean checkUser(int id);
}
