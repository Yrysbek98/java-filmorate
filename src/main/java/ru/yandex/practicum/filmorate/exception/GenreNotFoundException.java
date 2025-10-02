package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;

public class GenreNotFoundException extends AbstractDtoException {
    public GenreNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
