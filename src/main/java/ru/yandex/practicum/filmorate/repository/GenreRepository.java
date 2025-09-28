package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Genre;


import java.util.List;

public interface GenreRepository {
    int findGenreIdByName(String genreName);

    Genre getGenreById(int id);

    List<Genre> getAllGenres();
}
