package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;

public class InternalServerException extends AbstractDtoException {
    public InternalServerException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
