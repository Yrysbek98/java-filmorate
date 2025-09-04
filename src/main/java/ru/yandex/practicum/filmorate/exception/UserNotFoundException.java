package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends AbstractDtoException {
    public UserNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
