package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Review {
    private Integer reviewId;
    private String content;
    private Boolean isPositive;
    private int useful = 0;
    private Integer userId;
    private Integer filmId;
}
