package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;


import java.time.LocalDate;
import java.util.Collection;

@SpringBootTest
public class FilmControllerTest {

    @Autowired
    private FilmController filmController;

    @Autowired
    private InMemoryFilmStorage filmStorage;

    @BeforeEach
    void cleanUp() {
        filmStorage.clear();
    }

    @Test
    void getFilms() {
        Film film = new Film("Титаник", "В первом и последнем плавании шикарного «Титаника» встречаются двое.", LocalDate.of(1997, 12, 19), 194);
        Film film1 = new Film("Бойцовский клуб", "Терзаемый хронической бессонницей и отчаянно пытающийся вырваться из мучительно скучной жизни клерк встречает некоего Тайлера Дардена, харизматического торговца мылом с извращенной философией.", LocalDate.of(1999, 10, 15), 149);

        filmController.createFilm(film);
        filmController.createFilm(film1);

        Collection<Film> films = filmController.findAllFilms();
        System.out.println(films);
        Assertions.assertEquals(2, films.size(), "Неправильно выполнен метод получения фильмов");
    }

    @Test
    void createFilm() {
        Film film = new Film("Древний фильм", "Фильм до рождение кино", LocalDate.of(2000, 12, 12), 60);
        filmController.createFilm(film);

        Collection<Film> films = filmController.findAllFilms();
        Assertions.assertEquals(1, films.size(), "Неправильно выполнено добавление фильма");
    }


}