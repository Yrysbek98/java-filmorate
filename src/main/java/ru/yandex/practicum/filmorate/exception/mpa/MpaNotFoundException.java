package ru.yandex.practicum.filmorate.exception.mpa;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exception.AbstractDtoException;

public class MpaNotFoundException extends AbstractDtoException {
    public MpaNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
