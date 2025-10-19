package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;


public interface FilmRepository {

    Optional<Film> getFilmById(int id);

    Film getFilmByIdWithGenre(int id);

    List<Film> findAllFilms();

    Film createFilm(Film film);

    Optional<Film> changeFilm(Film film);

    boolean deleteFilm(int id);

    void addLike(int id, int userId);

    void deleteLike(int id, int userId);

    List<Film> getPopularFilms(Integer count, Integer genreId, Integer year);

    List<Film> getFilmsByIds(List<Integer> filmIds);

    // Получения списка фильмов по режиссеру
    List<Film> getFilmsByDirector(int directorId, String sortBy);

    List<Film> searchFilms(String query, String by);

    List<Film> getCommonFilms(int userId, int friendId);
}