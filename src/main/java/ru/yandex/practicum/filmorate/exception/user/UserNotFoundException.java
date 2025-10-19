package ru.yandex.practicum.filmorate.exception.user;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exception.AbstractDtoException;

public class UserNotFoundException extends AbstractDtoException {
    public UserNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
