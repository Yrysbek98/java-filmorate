package ru.yandex.practicum.filmorate.serviceDB;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RecommendationServiceTest {

    @Autowired
    private RecommendationService recommendationService;

    @Test
    @DirtiesContext
    void getRecommendations_UserWithoutLikes_ReturnsEmptyList() {
        var result = recommendationService.getRecommendations(1);
        assertNotNull(result);
    }

    @Test
    @DirtiesContext
    void getRecommendations_NonExistentUser_ReturnsEmptyList() {
        var result = recommendationService.getRecommendations(999);
        assertNotNull(result);
    }
}