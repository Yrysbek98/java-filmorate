package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;


public interface FilmRepository {


    List<Film> findAllFilms();

    Film createFilm(Film film);

    Film changeFilm(Film film);

    void addLike(int id, int userId);

    void deleteLike(int id, int userId);

    List<Film> getPopularFilms(int count);
}
