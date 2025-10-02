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


import java.util.List;


@Service
@RequiredArgsConstructor
public class BasedFilmService implements FilmServiceDB {
    final FilmRepository filmRepository;


    @Override
    public Film getFilmById(int id) {
        final Film f = filmRepository.getFilmById(id);
        if (f == null) {
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
        final MPA mpa = film.getMpa();
        if (mpa == null || mpa.getId() < 1 || mpa.getId() > 6) {
            throw new MpaNotFoundException("MPA not found");
        }

        final List<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            for (Genre genre : genres) {
                if (genre.getId() < 1 || genre.getId() > 7) {
                    throw new GenreNotFoundException("Genre not found");
                }
            }
        }

        return filmRepository.createFilm(film);

    }

    @Override
    public Film changeFilm(Film film) {
        final Film f = filmRepository.getFilmById(film.getId());
        if (f == null) {
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
        final Film f = filmRepository.getFilmById(id);
        if (f == null) {
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
        final Film f = filmRepository.getFilmById(id);
        if (f == null) {
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
