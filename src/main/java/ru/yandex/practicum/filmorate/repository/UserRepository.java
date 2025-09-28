package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserRepository {
    List<User> findAll();

    User createUser(User user);

    User changeUser(User user);

    void addFriend(int idOfUser, int idOfFriend);

    void deleteFriend(int idOfUser, int idOfFriend);

    List<User> getSameFriends(int idOfUser, int idOfFriend);

    List<User> getFriends(int idOfUser);

}
