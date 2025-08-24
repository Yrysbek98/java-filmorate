package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ru.yandex.practicum.filmorate.controller.UserController;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

@SpringBootTest
public class UserControllerTest {
    @Test
    void getUsers() {
        User user1 = new User(1, "resbek@gmail.com", "resbek", "Yrysbek", LocalDate.of(2000, 12, 5));
        User user2 = new User(2, "resbek98@gmail.com", "Yrysbek", "resbek", LocalDate.of(1998, 12, 5));
        UserController userController = new UserController();
        userController.create(user1);
        userController.create(user2);
        Collection<User> films = userController.findAll();
        Assertions.assertEquals(2, films.size(), "Неправильно выполнено метод получения пользователя");
    }

    @Test
    void createFilm() {
        User user1 = new User(1, "resbek@gmail.com", "resbek", "Yrysbek", LocalDate.of(2000, 12, 5));
        UserController userController = new UserController();
        userController.create(user1);
        Collection<User> films = userController.findAll();
        Assertions.assertEquals(1, films.size(), "Неправильно выполнено добавление пользователя");
    }

    @Test
    void changeFilm() {
        User user1 = new User(1, "resbek@gmail.com", "resbek", "Yrysbek", LocalDate.of(2000, 12, 5));
        User user2 = new User(1, "resbek98@gmail.com", "Yrysbek", "resbek", LocalDate.of(1998, 12, 5));
        UserController userController = new UserController();
        userController.create(user1);
        userController.change(user2);
        Collection<User> films = userController.findAll();
        Assertions.assertEquals(1, films.size(), "Неправильно выполнено метод изменение пользователя");

    }
}
