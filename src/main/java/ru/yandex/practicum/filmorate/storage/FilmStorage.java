package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> findAllFilms();

    Film createFilm(Film film);

    Film changeFilm(Film film);

    Film addLike(int id, int userId);

    Film deleteLike(int id, int userId);

    Collection<Film> getTopFilms(int count);
}
