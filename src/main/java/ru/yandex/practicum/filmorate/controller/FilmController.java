package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ErrorResponse;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.serviceDB.FilmServiceDB;


import java.util.List;


@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmServiceDB filmServiceDB;

    @GetMapping
    public List<Film> findAllFilms() {
        return filmServiceDB.findAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmByIdWithGenre(
            @PathVariable int id) {
        return filmServiceDB.getFilmByIdWithGenre(id);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmServiceDB.createFilm(film);
    }

    @PutMapping
    public Film changeFilm(@Valid @RequestBody Film film) {
        return filmServiceDB.changeFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable int id,
            @PathVariable int userId
    ) {
        filmServiceDB.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(
            @PathVariable int id,
            @PathVariable int userId
    ) {
        filmServiceDB.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(defaultValue = "10") int count) {
        return filmServiceDB.getPopularFilms(count);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleServerExceptions(AbstractDtoException exception) {
        ErrorResponse errorResponse = exception.toResponse();
        return new ResponseEntity<>(errorResponse, errorResponse.httpStatusCode());
    }

    @ExceptionHandler(FilmValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(FilmValidationException ex) {
        ErrorResponse errorResponse = ex.toResponse();
        return new ResponseEntity<>(errorResponse, errorResponse.httpStatusCode());
    }

    @ExceptionHandler(FilmNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(FilmNotFoundException ex) {
        ErrorResponse errorResponse = ex.toResponse();
        return new ResponseEntity<>(errorResponse, errorResponse.httpStatusCode());
    }

}
