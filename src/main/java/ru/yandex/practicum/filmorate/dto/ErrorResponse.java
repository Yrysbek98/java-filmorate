package ru.yandex.practicum.filmorate.dto;

import org.springframework.http.HttpStatusCode;

public record ErrorResponse(String description, HttpStatusCode httpStatusCode) {
}
