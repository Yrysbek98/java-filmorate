package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;

public class FilmValidationException extends AbstractDtoException {
    public FilmValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
