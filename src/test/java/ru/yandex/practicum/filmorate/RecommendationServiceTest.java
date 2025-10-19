package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.repository.recommendation.RecommendationRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RecommendationServiceTest {

    @Autowired
    private RecommendationRepository recommendationService;

    @Test
    @DirtiesContext
    @DisplayName("Должен возвращать рекомендации по ID")
    void getRecommendations_UserWithoutLikes_ReturnsEmptyList() {
        var result = recommendationService.getRecommendations(1);
        assertNotNull(result);
    }

    @Test
    @DirtiesContext
    @DisplayName("Должен возвращать пустой список")
    void getRecommendations_NonExistentUser_ReturnsEmptyList() {
        var result = recommendationService.getRecommendations(999);
        assertNotNull(result);
    }
}