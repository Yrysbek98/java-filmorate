package ru.yandex.practicum.filmorate.exception.director;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exception.AbstractDtoException;

public class DirectorNotFoundException extends AbstractDtoException {
    public DirectorNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
