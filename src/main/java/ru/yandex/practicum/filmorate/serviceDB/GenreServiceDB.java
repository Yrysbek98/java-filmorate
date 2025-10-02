package ru.yandex.practicum.filmorate.serviceDB;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;


public interface GenreServiceDB {
    int findGenreIdByName(String genreName);

    Genre getGenreById(int id);

    List<Genre> getAllGenres();
}
