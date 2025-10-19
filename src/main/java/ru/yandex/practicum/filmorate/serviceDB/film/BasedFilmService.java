package ru.yandex.practicum.filmorate.serviceDB.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.director.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.film.FilmValidationException;
import ru.yandex.practicum.filmorate.exception.genre.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.mpa.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.repository.director.DirectorRepository;
import ru.yandex.practicum.filmorate.repository.event.EventRepository;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepository;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepository;


import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BasedFilmService implements FilmServiceDB {
    final FilmRepository filmRepository;
    final MpaRepository mpaRepository;
    final GenreRepository genreRepository;
    final DirectorRepository directorRepository;
    final EventRepository eventRepository;

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

    // Получения списка фильмов по режиссеру
    @Override
    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        directorRepository.findDirectorById(directorId)
                .orElseThrow(() -> new DirectorNotFoundException("Режиссер с id =" + directorId + " не найден."));

        if (!"year".equals(sortBy) && !"likes".equals(sortBy)) {
            throw new FilmValidationException("Неверный параметр sortBy: " + sortBy +
                    ". Допустимые значения: 'year', 'likes'.");
        }

        return filmRepository.getFilmsByDirector(directorId, sortBy);
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        return filmRepository.getCommonFilms(userId, friendId);
    }

    @Override
    public Film createFilm(Film film) {
        Optional<Mpa> mpa = mpaRepository.getMpaById(film.getMpa().getId());
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

    // Удаление фильма по ID
    @Override
    public void deleteFilm(int id) {
        // Проверка существования фильма
        this.getFilmById(id);

        filmRepository.deleteFilm(id);
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
        Event event = new Event(
                System.currentTimeMillis(),
                userId,
                EventType.LIKE,
                Operation.ADD,
                id);
        eventRepository.addEvent(event);
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
        Event event = new Event(
                System.currentTimeMillis(),
                userId,
                EventType.LIKE,
                Operation.REMOVE,
                id);
        eventRepository.addEvent(event);
    }

    @Override
    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        if (count <= 0) {
            throw new FilmValidationException("Количество фильмов должно быть положительным числом");
        }

        return filmRepository.getPopularFilms(count, genreId, year);
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return filmRepository.searchFilms(query.trim().toLowerCase(), by);
    }
}
