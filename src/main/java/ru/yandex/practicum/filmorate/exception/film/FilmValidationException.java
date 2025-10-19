package ru.yandex.practicum.filmorate.exception.film;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exception.AbstractDtoException;

public class FilmValidationException extends AbstractDtoException {
    public FilmValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
