package ru.yandex.practicum.filmorate.exception.user;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exception.AbstractDtoException;

public class UserValidationException extends AbstractDtoException {
    public UserValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
