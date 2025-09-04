package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.dto.ErrorResponse;


public abstract  class AbstractDtoException extends RuntimeException {
    private HttpStatus httpStatus;
    public AbstractDtoException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
    public ErrorResponse toResponse(){
       return  new ErrorResponse(getMessage(), httpStatus);

    }
}
