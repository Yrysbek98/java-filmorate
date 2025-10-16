package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;

public class ReviewNotFoundException extends AbstractDtoException {

    public ReviewNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}

