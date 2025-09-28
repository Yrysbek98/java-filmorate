package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;

public class GenreValidationException extends AbstractDtoException {
    public GenreValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
