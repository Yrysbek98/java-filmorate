package ru.yandex.practicum.filmorate;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;



import java.time.LocalDate;
import java.util.Collection;

@SpringBootTest
public class FilmControllerTest {

    @Test
    void getFilms() {
        Film film = new Film(1, "Титатик", "В первом и последнем плавании шикарного «Титаника» встречаются двое.", LocalDate.of(1997, 12, 19), 194);
        Film film1 = new Film(2, "Бойцовский клуб", "Терзаемый хронической бессонницей и отчаянно пытающийся вырваться из мучительно скучной жизни клерк встречает некоего Тайлера Дардена, харизматического торговца мылом с извращенной философией.", LocalDate.of(1999, 10, 15), 149);
        FilmController filmController = new FilmController();
        filmController.create(film);
        filmController.create(film1);
        Collection<Film> films = filmController.findAll();
        Assertions.assertEquals(2, films.size(), "Неправильно выполнено метод получения фильмов");
    }

    @Test
    void createFilm() {
        Film film = new Film(1, "Древний фильм", "Фильм до рождение кино", LocalDate.of(2000, 12, 12), 60);
        FilmController filmController = new FilmController();
        filmController.create(film);
        Collection<Film> films = filmController.findAll();
        Assertions.assertEquals(1, films.size(), "Неправильно выполнено добавление фильма");
    }

    @Test
    void changeFilm() {
        Film film = new Film(1, "Древний фильм", "Фильм до рождение кино", LocalDate.of(2000, 12, 12), 60);
        FilmController filmController = new FilmController();
        filmController.create(film);
        Film film1 = new Film(1, "Современный фильм", "Фильм до рождение кино", LocalDate.of(2020, 12, 12), 0);
        filmController.change(film1);
        Collection<Film> films = filmController.findAll();
        Assertions.assertEquals(1, films.size(), "Неправильно выполнено метод изменение фильма");

    }

}
