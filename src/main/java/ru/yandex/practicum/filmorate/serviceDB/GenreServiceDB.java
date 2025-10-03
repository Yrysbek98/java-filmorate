package ru.yandex.practicum.filmorate.serviceDB;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;


public interface GenreServiceDB {
    int findGenreIdByName(String genreName);

    Optional<Genre> getGenreById(int id);

    List<Genre> getAllGenres();
}
