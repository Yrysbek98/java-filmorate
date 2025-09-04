package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class FilmNotFoundException extends AbstractDtoException {
    public FilmNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
