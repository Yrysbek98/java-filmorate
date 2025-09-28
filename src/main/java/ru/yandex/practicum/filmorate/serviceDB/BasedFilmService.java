package ru.yandex.practicum.filmorate.serviceDB;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.MpaRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasedFilmService implements FilmServiceDB {
    final FilmRepository filmRepository;
    final MpaRepository mpaRepository;
    final GenreRepository genreRepository;

    @Override
    public Optional<Film> getFilmById(int id) {
        final Film f = filmRepository.getFilmById(id)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с таким " + id + " не найден"));
        return Optional.ofNullable(f);
    }

    @Override
    public List<Film> findAllFilms() {
        return filmRepository.findAllFilms();
    }

    @Override
    public Film createFilm(Film film) {

        return null;
    }

    @Override
    public Film changeFilm(Film film) {
        final Film f = filmRepository.getFilmById(film.getId())
                .orElseThrow(() -> new FilmNotFoundException("Фильм с таким " + film.getId() + " не найден"));
        filmRepository.changeFilm(f);
        return f;
    }

    @Override
    public void addLike(int id, int userId) {
        if (id < 1) {
            throw new FilmValidationException("Некорректный id фильма");
        }
        if (userId < 1) {
            throw new FilmValidationException("Некорректный id пользователя");
        }
    }

    @Override
    public void deleteLike(int id, int userId) {
        if (id < 1) {
            throw new FilmValidationException("Некорректный id фильма");
        }
        if (userId < 1) {
            throw new FilmValidationException("Некорректный id пользователя");
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return List.of();
    }
}
