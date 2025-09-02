package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Set<Integer> likes = new HashSet<>();
    @Override
    public Collection<Film> findAll() {
        log.info("Get all films");
        return films.values();
    }

    @Override
    public Film create(Film film) {
        log.info("Create film: id={}, name={}", film.getId(), film.getName());
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film change(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Фильм с таким ID не найден");
        }
        log.info("Change film: id={}, name={}", film.getId(), film.getName());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void addLike(int id) {
            likes.add(id);
    }

    @Override
    public void deleteLike(int id) {
        likes.remove(id);
    }

    @Override
    public Collection<Film> getTopFilms() {
        return List.of();
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
