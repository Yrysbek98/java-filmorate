package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Get all users");
        return users.values();
    }

    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody User user) {
        log.info("Create a new user={}", user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        String displayName = user.getDisplayName();
        return ResponseEntity.ok("Пользователь добавлен: " + displayName);
    }

    @PutMapping
    public ResponseEntity<String> change(@Valid @RequestBody User user) {
        log.info("Change user={}", user);
        users.put(user.getId(), user);
        String displayName = user.getDisplayName();
        return ResponseEntity.ok("Пользователь изменен: " + displayName);
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
