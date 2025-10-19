package ru.yandex.practicum.filmorate.exception.mpa;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exception.AbstractDtoException;

public class MpaValidationException extends AbstractDtoException {
    public MpaValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
