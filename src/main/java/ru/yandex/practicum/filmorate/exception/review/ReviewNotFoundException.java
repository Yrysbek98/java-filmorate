package ru.yandex.practicum.filmorate.exception.review;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exception.AbstractDtoException;

public class ReviewNotFoundException extends AbstractDtoException {

    public ReviewNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}

