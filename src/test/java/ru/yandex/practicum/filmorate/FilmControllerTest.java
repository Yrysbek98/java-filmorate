package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

@SpringBootTest
public class FilmControllerTest {

    @Autowired
    private FilmController filmController;

    @Test
    void getFilms() {
        Film film = new Film(1, "Титаник", "В первом и последнем плавании шикарного «Титаника» встречаются двое.", LocalDate.of(1997, 12, 19), 194);
        Film film1 = new Film(2, "Бойцовский клуб", "Терзаемый хронической бессонницей и отчаянно пытающийся вырваться из мучительно скучной жизни клерк встречает некоего Тайлера Дардена, харизматического торговца мылом с извращенной философией.", LocalDate.of(1999, 10, 15), 149);

        filmController.createFilm(film);
        filmController.createFilm(film1);

        Collection<Film> films = filmController.findAllFilms();
        Assertions.assertEquals(2, films.size(), "Неправильно выполнен метод получения фильмов");
    }

    @Test
    void createFilm() {
        Film film = new Film(0, "Древний фильм", "Фильм до рождение кино", LocalDate.of(2000, 12, 12), 60);
        filmController.createFilm(film);

        Collection<Film> films = filmController.findAllFilms();
        Assertions.assertEquals(1, films.size(), "Неправильно выполнено добавление фильма");
    }

    @Test
    void changeFilm() {
        Film film = new Film(0, "Древний фильм", "Фильм до рождение кино", LocalDate.of(2000, 12, 12), 60);
        filmController.createFilm(film);

        Film updatedFilm = new Film(film.getId(), "Современный фильм", "Обновленное описание", LocalDate.of(2020, 12, 12), 120);
        filmController.changeFilm(updatedFilm);

        Collection<Film> films = filmController.findAllFilms();
        Assertions.assertEquals(1, films.size(), "Неправильно выполнен метод изменения фильма");

        Film storedFilm = films.iterator().next();
        Assertions.assertEquals("Современный фильм", storedFilm.getName(), "Название фильма не обновлено");
        Assertions.assertEquals(120, storedFilm.getDuration(), "Продолжительность фильма не обновлена");
    }
}