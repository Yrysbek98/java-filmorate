package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;

public class MpaValidationException extends AbstractDtoException {
    public MpaValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
