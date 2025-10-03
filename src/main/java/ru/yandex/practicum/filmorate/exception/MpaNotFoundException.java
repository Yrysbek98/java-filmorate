package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;

public class MpaNotFoundException extends AbstractDtoException {
    public MpaNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
