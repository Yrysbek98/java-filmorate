package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmControllerCommonFilmsTest {

    @Autowired
    private FilmRepository filmDbStorage;

    @Autowired
    private UserRepository userDbStorage;

    private Film film1;
    private Film film2;
    private Film film3;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // создаём пользователей
        user1 = new User(0, "user1@mail.com", "user1", "User One", LocalDate.of(1990, 1, 1));
        user2 = new User(0, "user2@mail.com", "user2", "User Two", LocalDate.of(1992, 2, 2));
        user1 = userDbStorage.createUser(user1);
        user2 = userDbStorage.createUser(user2);

        // создаём фильмы
        film1 = new Film(0, "Film One", "Desc 1", LocalDate.of(2000, 1, 1), 100, new Mpa(1, "G"), List.of());
        film2 = new Film(0, "Film Two", "Desc 2", LocalDate.of(2001, 1, 1), 110, new Mpa(1, "G"), List.of());
        film3 = new Film(0, "Film Three", "Desc 3", LocalDate.of(2002, 1, 1), 120, new Mpa(1, "G"), List.of());
        film1 = filmDbStorage.createFilm(film1);
        film2 = filmDbStorage.createFilm(film2);
        film3 = filmDbStorage.createFilm(film3);

        // добавляем лайки
        filmDbStorage.addLike(film1.getId(), user1.getId());
        filmDbStorage.addLike(film1.getId(), user2.getId()); // общий лайк

        filmDbStorage.addLike(film2.getId(), user1.getId()); // только user1
        filmDbStorage.addLike(film3.getId(), user2.getId()); // только user2
    }

    @Test
    @DisplayName("getCommonFilms должен возвращать только общие фильмы по лайкам")
    void shouldReturnCommonFilms() {
        List<Film> common = filmDbStorage.getCommonFilms(user1.getId(), user2.getId());

        assertThat(common)
                .hasSize(1)
                .extracting(Film::getName)
                .containsExactly("Film One");
    }

    @Test
    @DisplayName("getCommonFilms должен вернуть пустой список, если нет общих лайков")
    void shouldReturnEmptyListIfNoCommonLikes() {
        // удаляем общий лайк
        filmDbStorage.deleteLike(film1.getId(), user2.getId());

        List<Film> common = filmDbStorage.getCommonFilms(user1.getId(), user2.getId());

        assertThat(common).isEmpty();
    }
}
