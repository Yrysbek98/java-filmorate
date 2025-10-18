package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ErrorResponse;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.serviceDB.FilmServiceDB;

import java.util.List;
import java.util.Optional;

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
    public Film getFilmByIdWithGenre(@PathVariable int id) {
        return filmServiceDB.getFilmByIdWithGenre(id);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmServiceDB.createFilm(film);
    }

    @PutMapping
    public Optional<Film> changeFilm(@Valid @RequestBody Film film) {
        return filmServiceDB.changeFilm(film);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable @Positive int id) {
        log.info("Выполнение метода deleteFilm.");
        filmServiceDB.deleteFilm(id);
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
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year) {
        return filmServiceDB.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(
            @PathVariable int directorId,
            @RequestParam String sortBy) {
        return filmServiceDB.getFilmsByDirector(directorId, sortBy);
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

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ErrorResponse> handleEmptyResult(EmptyResultDataAccessException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Запрашиваемый фильм не найден.",
                HttpStatus.NOT_FOUND
        );

        return new ResponseEntity<>(errorResponse, errorResponse.httpStatusCode());
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query,
                                  @RequestParam String by) {
        log.info("Поиск фильмов по запросу '{}' по полям '{}'", query, by);
        return filmServiceDB.searchFilms(query, by);
    }
}
