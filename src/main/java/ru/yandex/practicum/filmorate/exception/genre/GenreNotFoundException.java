package ru.yandex.practicum.filmorate.exception.genre;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exception.AbstractDtoException;

public class GenreNotFoundException extends AbstractDtoException {
    public GenreNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
