package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.dto.ErrorResponse;
import ru.yandex.practicum.filmorate.exception.AbstractDtoException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;


import java.util.Collection;


@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User changeUser(@Valid @RequestBody User user) {
        return userService.changeUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable int id,
            @PathVariable int friendId
    ) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(
            @PathVariable int id,
            @PathVariable int friendId
    ) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public Collection<User> getSameFriends(
            @PathVariable int id,
            @PathVariable int otherId
    ) {
        return userService.getSameFriends(id, otherId);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleServerExceptions(AbstractDtoException exception) {
        ErrorResponse errorResponse = exception.toResponse();
        return new ResponseEntity<>(errorResponse, errorResponse.httpStatusCode());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNotFound(AbstractDtoException exception) {
        ErrorResponse errorResponse = exception.toResponse();
        return new ResponseEntity<>(errorResponse, errorResponse.httpStatusCode());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleValidationException(AbstractDtoException exception) {
        ErrorResponse errorResponse = exception.toResponse();
        return new ResponseEntity<>(errorResponse, errorResponse.httpStatusCode());
    }

}
