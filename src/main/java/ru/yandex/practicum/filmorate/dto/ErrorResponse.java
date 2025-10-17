package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatusCode;

public record ErrorResponse(
        @JsonProperty("error") String description,
        HttpStatusCode httpStatusCode) {
}

