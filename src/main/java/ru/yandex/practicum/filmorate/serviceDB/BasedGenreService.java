package ru.yandex.practicum.filmorate.serviceDB;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.GenreValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.List;
import java.util.Optional;


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
    public Optional<Genre> getGenreById(int id) {
        if (genreRepository.getGenreById(id).isEmpty()) {
            throw new GenreNotFoundException("Жанр с таким id= " + id + " не найден");
        }
        return genreRepository.getGenreById(id);
    }

    @Override
    public List<Genre> getAllGenres() {
        return genreRepository.getAllGenres();
    }
}
