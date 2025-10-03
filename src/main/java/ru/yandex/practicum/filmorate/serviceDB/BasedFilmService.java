package ru.yandex.practicum.filmorate.serviceDB;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
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
        final Optional<Film> f = filmRepository.getFilmById(id);
        if (f.isEmpty()) {
            throw new FilmNotFoundException("Фильм с таким " + id + " не найден");
        }
        return filmRepository.getFilmById(id);
    }

    @Override
    public Film getFilmByIdWithGenre(int id) {

        return filmRepository.getFilmByIdWithGenre(id);
    }

    @Override
    public List<Film> findAllFilms() {
        return filmRepository.findAllFilms();
    }

    @Override
    public Film createFilm(Film film) {
        Optional<MPA> mpa = mpaRepository.getMpaById(film.getMpa().getId());
        if (mpa.isEmpty()) {
            throw new MpaNotFoundException("Рейтинг с таким id= " + film.getMpa().getId() + " не найден");
        }

        final List<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            for (Genre genre : genres) {
                if (genreRepository.getGenreById(genre.getId()).isEmpty()) {
                    throw new GenreNotFoundException("Жанр с таким id= " + genre.getId() + " не найден");
                }
            }
        }

        return filmRepository.createFilm(film);

    }

    @Override
    public Optional<Film> changeFilm(Film film) {
        final Optional<Film> f = filmRepository.getFilmById(film.getId());
        if (f.isEmpty()) {
            throw new FilmNotFoundException("Фильм с таким " + film.getId() + " не найден");
        }

        return filmRepository.changeFilm(film);
    }

    @Override
    public void addLike(int id, int userId) {
        if (id < 1) {
            throw new FilmValidationException("Некорректный id фильма");
        }
        if (userId < 1) {
            throw new FilmValidationException("Некорректный id пользователя");
        }
        final Optional<Film> f = filmRepository.getFilmById(id);
        if (f.isEmpty()) {
            throw new FilmNotFoundException("Фильм с таким " + id + " не найден");
        }
        filmRepository.addLike(id, userId);
    }

    @Override
    public void deleteLike(int id, int userId) {
        if (id < 1) {
            throw new FilmValidationException("Некорректный id фильма");
        }
        if (userId < 1) {
            throw new FilmValidationException("Некорректный id пользователя");
        }
        final Optional<Film> f = filmRepository.getFilmById(id);
        if (f.isEmpty()) {
            throw new FilmNotFoundException("Фильм с таким " + id + " не найден");
        }
        filmRepository.deleteLike(id, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new FilmValidationException("Количество фильмов должно быть положительным числом");
        }

        return filmRepository.getPopularFilms(count);
    }
}
