package ru.yandex.practicum.filmorate.exception.review;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exception.AbstractDtoException;

public class ReviewValidationException  extends AbstractDtoException {
    public ReviewValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
