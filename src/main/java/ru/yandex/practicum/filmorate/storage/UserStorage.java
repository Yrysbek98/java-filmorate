package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User change(User user);

    void addFriend(int idOfUser, int idOfFriend);

    void deleteFriend(int idOfUser, int idOfFriend);

    Collection<User> getAllFriends();

   Set<Integer> getSetOfLikes(int id );
}
