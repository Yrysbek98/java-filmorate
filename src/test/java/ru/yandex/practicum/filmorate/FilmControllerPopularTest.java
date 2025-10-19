package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class FilmControllerPopularTest {
    @Autowired
    private FilmController filmController;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private UserRepository userRepository;


    private Film film1;
    private Film film2;
    private Film film3;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // Создаём пользователей
        user1 = userRepository.createUser(
                new User(0, "user1@mail.com", "user1", "User One", LocalDate.of(1990, 1, 1))
        );
        user2 = userRepository.createUser(
                new User(0, "user2@mail.com", "user2", "User Two", LocalDate.of(1992, 2, 2))
        );

        // Создаём фильмы
        film1 = filmRepository.createFilm(new Film(
                0, "Film One", "Desc1", LocalDate.of(2020, 5, 10), 120,
                new Mpa(1, "G"), new ArrayList<>()
        ));
        film2 = filmRepository.createFilm(new Film(
                0, "Film Two", "Desc2", LocalDate.of(2021, 6, 15), 90,
                new Mpa(1, "G"), new ArrayList<>()
        ));
        film3 = filmRepository.createFilm(new Film(
                0, "Film Three", "Desc3", LocalDate.of(2020, 3, 8), 100,
                new Mpa(1, "G"), new ArrayList<>()
        ));


        filmRepository.addLike(film1.getId(), user1.getId());
        filmRepository.addLike(film1.getId(), user2.getId());
        filmRepository.addLike(film2.getId(), user1.getId());
    }

    @Test
    @DisplayName("getPopularFilms без параметров — возвращает фильмы по количеству лайков")
    void shouldReturnPopularFilms() {
        List<Film> popular = filmController.getPopularFilms(10, null, null);

        assertThat(popular).hasSize(3);
        assertThat(popular.get(0).getName()).isEqualTo("Film One"); // 2 лайка
        assertThat(popular.get(1).getName()).isEqualTo("Film Two"); // 1 лайк
        assertThat(popular.get(2).getName()).isEqualTo("Film Three"); // 0 лайков
    }

    @Test
    @DisplayName("getPopularFilms с параметром year — фильтрует по году выпуска")
    void shouldReturnPopularFilmsByYear() {
        List<Film> popular2020 = filmController.getPopularFilms(10, null, 2020);

        assertThat(popular2020).hasSize(2);
        assertThat(popular2020)
                .extracting(Film::getName)
                .containsExactlyInAnyOrder("Film One", "Film Three");
    }

}
