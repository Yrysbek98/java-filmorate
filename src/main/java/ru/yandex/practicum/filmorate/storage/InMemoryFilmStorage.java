package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final InMemoryUserStorage userStorage = new InMemoryUserStorage();

    @Override
    public Collection<Film> findAllFilms() {
        log.info("Get all films");
        return films.values();
    }

    @Override
    public Film createFilm(Film film) {
        log.info("Create film: id={}, name={}", film.getId(), film.getName());
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film changeFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Фильм с таким ID не найден");
        }
        log.info("Change film: id={}, name={}", film.getId(), film.getName());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film addLike(int id, int userId) {
        if (id < 1) {
            throw new FilmValidationException("Некорректный id фильма");
        }
        if (userId < 1) {
            throw new FilmValidationException("Некорректный id пользователя");
        }
        Film film = films.get(id);
        if (film == null) {
            throw new FilmNotFoundException("Фильм с таким" + id + " не найден");
        }
        if (!userStorage.checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с таким" + userId + " не найден");
        }
        Set<Integer> likes = film.getLikes();
        likes.add(userId);
        film.setLikes(likes);
        return film;
    }

    @Override
    public Film deleteLike(int id, int userId) {
        if (id < 1) {
            throw new FilmValidationException("Некорректный id фильма");
        }
        if (userId < 1) {
            throw new FilmValidationException("Некорректный id пользователя");
        }
        Film film = films.get(id);
        if (film == null) {
            throw new FilmNotFoundException("Фильм с таким" + id + " не найден");
        }
        if (!userStorage.checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с таким" + userId + " не найден");
        }
        Set<Integer> likes = film.getLikes();
        likes.remove(userId);
        film.setLikes(likes);
        return film;
    }

    @Override
    public Collection<Film> getTopFilms(int count) {
        if (count <= 0) {
            throw new FilmValidationException("Количество фильмов должно быть положительным числом");
        }
        return films.values().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
