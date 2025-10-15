package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;

public class ReviewValidationException  extends AbstractDtoException {
    public ReviewValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
