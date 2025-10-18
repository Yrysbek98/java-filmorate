package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;

public class DirectorValidationException extends AbstractDtoException {
    public DirectorValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
