package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> getUserById(int id);

    List<User> findAll();

    User createUser(User user);

    User changeUser(User user);

    void addFriend(int idOfUser, int idOfFriend);

    void deleteFriend(int idOfUser, int idOfFriend);

    List<User> getSameFriends(int idOfUser, int idOfFriend);

    List<User> getFriends(int idOfUser);

}
