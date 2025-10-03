package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import ru.yandex.practicum.filmorate.dto.ErrorResponse;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.serviceDB.GenreServiceDB;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreServiceDB genreServiceDB;

    @GetMapping
    public List<Genre> getAllGenres() {
        return genreServiceDB.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) {
        return genreServiceDB.getGenreById(id)
                .orElseThrow(() -> new GenreNotFoundException("Жанр с id=" + id + " не найден"));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleServerExceptions(AbstractDtoException exception) {
        ErrorResponse errorResponse = exception.toResponse();
        return new ResponseEntity<>(errorResponse, errorResponse.httpStatusCode());
    }

    @ExceptionHandler(GenreValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(GenreValidationException ex) {
        ErrorResponse errorResponse = ex.toResponse();
        return new ResponseEntity<>(errorResponse, errorResponse.httpStatusCode());
    }

    @ExceptionHandler(GenreNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(GenreNotFoundException ex) {
        ErrorResponse errorResponse = ex.toResponse();
        return new ResponseEntity<>(errorResponse, errorResponse.httpStatusCode());
    }
}
