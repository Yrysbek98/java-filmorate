package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

@SpringBootTest
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @Test
    void getUsers() {
        User user1 = new User(0, "resbek@gmail.com", "resbek", "Yrysbek", LocalDate.of(2000, 12, 5));
        User user2 = new User(0, "resbek98@gmail.com", "Yrysbek", "resbek", LocalDate.of(1998, 12, 5));

        userController.createUser(user1);
        userController.createUser(user2);

        Collection<User> users = userController.findAll();
        Assertions.assertEquals(4, users.size(), "Неправильно выполнен метод получения пользователей");
    }

    @Test
    void createUser() {
        User user1 = new User(0, "resbek@gmail.com", "resbek", "Yrysbek", LocalDate.of(2000, 12, 5));

        userController.createUser(user1);

        Collection<User> users = userController.findAll();
        Assertions.assertEquals(2, users.size(), "Неправильно выполнено добавление пользователя");


        User storedUser = users.iterator().next();
        Assertions.assertEquals("Yrysbek", storedUser.getName(), "Имя пользователя не установлено корректно");
    }

    @Test
    void changeUser() {
        User user1 = new User(1, "resbek@gmail.com", "resbek", "Yrysbek", LocalDate.of(2000, 12, 5));
        userController.createUser(user1);


        User updatedUser = new User(user1.getId(), "resbek98@gmail.com", "resbek_updated", "Res", LocalDate.of(1998, 12, 5));
        userController.changeUser(updatedUser);

        Collection<User> users = userController.findAll();
        Assertions.assertEquals(1, users.size(), "Неправильно выполнен метод изменения пользователя");

        User storedUser = users.iterator().next();
        Assertions.assertEquals("resbek_updated", storedUser.getLogin(), "Логин не обновился");
        Assertions.assertEquals("Res", storedUser.getName(), "Имя не обновилось");
        Assertions.assertEquals("resbek98@gmail.com", storedUser.getEmail(), "Email не обновился");
    }
}