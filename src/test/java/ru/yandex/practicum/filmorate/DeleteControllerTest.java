package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class DeleteControllerTest {

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    @Autowired
    private UserController userController;

    @Autowired
    private FilmController filmController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilmRepository filmRepository;

    private User user1;
    private Film film1;

    @BeforeEach
    void setup() {
        jdbc.getJdbcTemplate().execute("DELETE FROM friends");
        jdbc.getJdbcTemplate().execute("DELETE FROM likes");
        jdbc.getJdbcTemplate().execute("DELETE FROM reviews");
        jdbc.getJdbcTemplate().execute("DELETE FROM film_genres");
        jdbc.getJdbcTemplate().execute("DELETE FROM genres");
        jdbc.getJdbcTemplate().execute("DELETE FROM film_director");
        jdbc.getJdbcTemplate().execute("DELETE FROM films");
        jdbc.getJdbcTemplate().execute("DELETE FROM directors");
        jdbc.getJdbcTemplate().execute("DELETE FROM events");
        jdbc.getJdbcTemplate().execute("DELETE FROM users");

        // создаем пользователя
        user1 = new User(0, "user@mail.com", "userlogin", "User Name", LocalDate.of(1990, 1, 1));
        user1 = userRepository.createUser(user1);

        // создаем фильм
        film1 = new Film(0, "Test Film", "Description", LocalDate.of(2000, 1, 1),
                120, new Mpa(1, "G"), List.of());
        film1 = filmRepository.createFilm(film1);
    }

    @Test
    @DisplayName("deleteUser должен удалять пользователя по id")
    void shouldDeleteUserById() {
        userRepository.deleteUser(user1.getId());

        List<User> users = userRepository.findAll();
        assertTrue(users.isEmpty(), "Список пользователей должен быть пуст после удаления");
    }

    @Test
    @DisplayName("deleteFilm должен удалять фильм по id")
    void shouldDeleteFilmById() {
        filmRepository.deleteFilm(film1.getId());

        List<Film> films = filmRepository.findAllFilms();
        assertTrue(films.isEmpty(), "Список фильмов должен быть пуст после удаления");
    }
}
