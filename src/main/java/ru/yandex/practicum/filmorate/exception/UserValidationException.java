package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;

public class UserValidationException extends AbstractDtoException {
    public UserValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
