package ru.yandex.practicum.filmorate.exception.genre;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exception.AbstractDtoException;

public class GenreValidationException extends AbstractDtoException {
    public GenreValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
