package ru.yandex.practicum.filmorate.repository.recommendation;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;


public interface RecommendationRepository {

    List<Film> getRecommendations(int userId);

}
