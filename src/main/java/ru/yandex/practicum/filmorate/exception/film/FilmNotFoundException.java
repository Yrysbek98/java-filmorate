package ru.yandex.practicum.filmorate.exception.film;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exception.AbstractDtoException;


public class FilmNotFoundException extends AbstractDtoException {
    public FilmNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
