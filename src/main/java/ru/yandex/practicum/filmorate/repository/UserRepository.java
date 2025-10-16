package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {

    Optional<User> getUserById(int id);

    List<User> findAll();

    User createUser(User user);

    Optional<User> changeUser(User user);

    void addFriend(int idOfUser, int idOfFriend);

    void deleteFriend(int idOfUser, int idOfFriend);

    List<User> getSameFriends(int idOfUser, int idOfFriend);

    List<User> getFriends(int idOfUser);

    Map<Integer, Set<Integer>> getAllUsersLikes();

    List<Integer> getLikedFilmsByUser(int userId);
}


