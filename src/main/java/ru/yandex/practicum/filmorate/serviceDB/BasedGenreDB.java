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
public class BasedGenreDB implements GenreServiceDB {
    final GenreRepository genreRepository;

    @Override
    public int findGenreIdByName(String genreName) {
        int id = genreRepository.findGenreIdByName(genreName);
        final Genre g = genreRepository.getGenreById(id).orElseThrow(() -> new GenreNotFoundException("Жанр с таким " + genreName + " не надйен"));
        return id;
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        if (id < 1) {
            throw new GenreValidationException("Некорректный id жанра");
        }
        final Genre g = genreRepository.getGenreById(id).orElseThrow(() -> new GenreNotFoundException("Жанр с таким " + id + " не надйен"));
        return Optional.ofNullable(g);
    }

    @Override
    public List<Genre> getAllGenres() {
        return genreRepository.getAllGenres();
    }
}
