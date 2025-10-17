package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;

public class DirectorNotFoundException extends AbstractDtoException{
    public DirectorNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
