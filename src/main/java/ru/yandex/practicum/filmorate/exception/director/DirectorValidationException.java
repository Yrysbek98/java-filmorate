package ru.yandex.practicum.filmorate.exception.director;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exception.AbstractDtoException;

public class DirectorValidationException extends AbstractDtoException {
    public DirectorValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
