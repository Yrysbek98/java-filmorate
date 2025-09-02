package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> findAll();

    Film create(Film film);

     Film change(Film film);

     void addLike(int id);

     void deleteLike(int id);

    Collection<Film> getTopFilms(int count);
}
