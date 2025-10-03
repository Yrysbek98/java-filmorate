package ru.yandex.practicum.filmorate.serviceStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User changeUser(User user) {
        return userStorage.changeUser(user);
    }

    public void addFriend(int idOfUser, int idOfFriend) {
        userStorage.addFriend(idOfUser, idOfFriend);
    }

    public void deleteFriend(int idOfUser, int idOfFriend) {
        userStorage.deleteFriend(idOfUser, idOfFriend);
    }

    public Collection<User> getSameFriends(int idOfUser, int idOfFriend) {
        return userStorage.getSameFriends(idOfUser, idOfFriend);
    }

    public Collection<User> getFriends(int idOfUser) {
        return userStorage.getFriends(idOfUser);
    }


}
