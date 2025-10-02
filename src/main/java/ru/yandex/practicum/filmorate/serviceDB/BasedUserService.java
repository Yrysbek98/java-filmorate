package ru.yandex.practicum.filmorate.serviceDB;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasedUserService implements UserServiceDB {

    final UserRepository userRepository;

    @Override
    public Optional<User> getUserById(int id) {
        final User u = userRepository.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким " + id + " не найден"));
        return Optional.ofNullable(u);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userRepository.createUser(user);
    }

    @Override
    public Optional<User> changeUser(User user) {
        final User u = userRepository.getUserById(user.getId())
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким " + user.getId() + " не найден"));

        if (u.getName() == null || u.getName().isBlank()) {
            u.setName(u.getLogin());
        }

        return userRepository.changeUser(user);
    }

    @Override
    public void addFriend(int idOfUser, int idOfFriend) {
        if (idOfUser == idOfFriend) {
            throw new UserValidationException("Нельзя добавлять в друзья самого себя");
        }
        final User u = userRepository.getUserById(idOfUser)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким " + idOfUser + " не найден"));
        final User fr = userRepository.getUserById(idOfFriend)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким " + idOfFriend + " не найден"));
        userRepository.addFriend(u.getId(), fr.getId());
    }

    @Override
    public void deleteFriend(int idOfUser, int idOfFriend) {
        if (idOfUser == idOfFriend) {
            throw new UserValidationException("Нельзя удалять самого себя из списка друзей");
        }
        final User u = userRepository.getUserById(idOfUser)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким " + idOfUser + " не найден"));
        final User fr = userRepository.getUserById(idOfFriend)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким " + idOfFriend + " не найден"));
        userRepository.deleteFriend(u.getId(), fr.getId());
    }

    @Override
    public List<User> getSameFriends(int idOfUser, int idOfFriend) {
        if (idOfUser == idOfFriend) {
            throw new UserValidationException("Нельзя искать общих друзей у самого себя");
        }
        return userRepository.getSameFriends(idOfUser, idOfFriend);
    }

    @Override
    public List<User> getFriends(int idOfUser) {
        final User u = userRepository.getUserById(idOfUser)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким " + idOfUser + " не найден"));
        return userRepository.getFriends(u.getId());
    }
}
