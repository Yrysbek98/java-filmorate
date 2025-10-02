package ru.yandex.practicum.filmorate.serviceDB;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.GenreValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class BasedGenreService implements GenreServiceDB {
    final GenreRepository genreRepository;

    @Override
    public int findGenreIdByName(String genreName) {
        int id = genreRepository.findGenreIdByName(genreName);
        return id;
    }

    @Override
    public Genre getGenreById(int id) {
        if (id < 1) {
            throw new GenreValidationException("Некорректный id жанра");
        }
        if (id > 6) {
            throw new GenreNotFoundException("Не найден такой жанр");
        }
        return genreRepository.getGenreById(id);
    }

    @Override
    public List<Genre> getAllGenres() {
        return genreRepository.getAllGenres();
    }
}
