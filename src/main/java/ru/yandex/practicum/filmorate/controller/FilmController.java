package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/film")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll(){
        return films.values();
    }

    @PostMapping
    public  Film create(@Valid @RequestBody Film film){
        if (film.getDescription() == null || film.getDescription().isBlank()) {
            throw new NullPointerException("Описание не может быть пустым");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film change(@Valid @RequestBody Film film){
        if (film.getDescription() == null || film.getDescription().isBlank()) {
            throw new NullPointerException("Описание не может быть пустым");
        }
        films.put(film.getId(), film);
        return film;
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
