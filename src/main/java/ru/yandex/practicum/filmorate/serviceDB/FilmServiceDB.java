package ru.yandex.practicum.filmorate.serviceDB;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;


public interface FilmServiceDB {

    Film getFilmById(int id);

    Film getFilmByIdWithGenre(int id);

    List<Film> findAllFilms();

    Film createFilm(Film film);

    Film changeFilm(Film film);

    void addLike(int id, int userId);

    void deleteLike(int id, int userId);

    List<Film> getPopularFilms(int count);
}
