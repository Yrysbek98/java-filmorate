package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ErrorResponse;
import ru.yandex.practicum.filmorate.exception.*;

import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.serviceDB.MpaServiceDB;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaServiceDB mpaServiceDB;

    @GetMapping
    public List<MPA> findAllMPA() {
        return mpaServiceDB.getAllMpa();
    }

    @GetMapping("/id")
    public Optional<MPA> getMpaById(
            @PathVariable int id) {
        return mpaServiceDB.getMpaById(id);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleServerExceptions(AbstractDtoException exception) {
        ErrorResponse errorResponse = exception.toResponse();
        return new ResponseEntity<>(errorResponse, errorResponse.httpStatusCode());
    }

    @ExceptionHandler(MpaValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MpaValidationException ex) {
        ErrorResponse errorResponse = ex.toResponse();
        return new ResponseEntity<>(errorResponse, errorResponse.httpStatusCode());
    }

    @ExceptionHandler(MpaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(MpaNotFoundException ex) {
        ErrorResponse errorResponse = ex.toResponse();
        return new ResponseEntity<>(errorResponse, errorResponse.httpStatusCode());
    }
}
