package ru.yandex.practicum.filmorate.serviceDB;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmServiceDB {

    Optional<Film> getFilmById(int id);

    Film getFilmByIdWithGenre(int id);

    List<Film> findAllFilms();

    Film createFilm(Film film);

    Optional<Film> changeFilm(Film film);

    void deleteFilm(int id);

    void addLike(int id, int userId);

    void deleteLike(int id, int userId);

    List<Film> getPopularFilms(int count, Integer genreId, Integer year);

    // Получения списка фильмов по режиссеру
    List<Film> getFilmsByDirector(int directorId, String sortBy);

    List<Film> searchFilms(String query, String by);

    List<Film> getCommonFilms(int userId, int friendId);
}