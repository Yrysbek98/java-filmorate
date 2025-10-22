package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.ReviewController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.review.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReviewControllerTest {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private ReviewController reviewController;

    private Review review1;
    private Review review2;
    private User user1;
    private User user2;
    private Film film1;
    private Film film2;


    @BeforeEach
    void setUp() {
        user1 = userRepository.createUser(new User(1, "user1@mail.com", "user1", "User One", LocalDate.of(1990, 1, 1)));
        user2 = userRepository.createUser(new User(2, "user2@mail.com", "user2", "User Two", LocalDate.of(1992, 2, 2)));

        film1 = filmRepository.createFilm(new Film(1, "Film One", "Desc", LocalDate.of(2000, 1, 1), 120, new Mpa(1, "G"), List.of()));
        film2 = filmRepository.createFilm(new Film(2, "Film Two", "Desc", LocalDate.of(2001, 1, 1), 130, new Mpa(1, "PG"), List.of()));

        review1 = reviewRepository.createReview(new Review(1, "Отлично", true, 5, user1.getId(), film1.getId()));
        review2 = reviewRepository.createReview(new Review(2, "Хорошо", true, 3, user2.getId(), film1.getId()));
    }

    @Test
    @DisplayName("getAllReviews должен вернуть все отзывы")
    void shouldReturnAllReviews() {
        List<Review> result = reviewController.getAllReviews(film1.getId(), 10);

        assertThat(result).hasSize(2)
                .extracting(Review::getContent)
                .containsExactlyInAnyOrder("Отлично", "Хорошо");
    }

    @Test
    @DisplayName("getAllReviews должен вернуть отзывы по конкретному фильму")
    void shouldReturnReviewsByFilmId() {
        List<Review> result = reviewController.getAllReviews(film1.getId(), 10);

        assertThat(result).hasSize(2)
                .allMatch(r -> r.getFilmId() == film1.getId());
    }

    @Test
    @DisplayName("getReviewById должен вернуть конкретный отзыв")
    void shouldReturnReviewById() {
        Review result = reviewController.getReviewById(review1.getReviewId());

        assertNotNull(result);
        assertEquals("Отлично", result.getContent());
    }

    @Test
    @DisplayName("createReview должен добавить отзыв в базу")
    void shouldCreateReview() {
        Review review3 = new Review(3, "Средне", false, 0, user1.getId(), film2.getId());
        Review saved = reviewController.createReview(review3);

        assertTrue(saved.getReviewId() > 0);
        assertEquals("Средне", saved.getContent());

        List<Review> all = reviewRepository.getAllReviews(user1.getId(), 10);
        assertThat(all).hasSize(2);
    }

    @Test
    @DisplayName("updateReview должен обновить отзыв")
    void shouldUpdateReview() {
        review1.setContent("Супер");
        review1.setUseful(10);

        Review updated = reviewController.updateReview(review1);

        assertNotNull(updated);
        assertEquals("Супер", updated.getContent());
        assertEquals(10, updated.getUseful());
    }

    @Test
    @DisplayName("deleteReview должен удалить отзыв")
    void shouldDeleteReview() {
        reviewController.deleteReview(review1.getReviewId());

        Optional<Review> result = reviewRepository.getReviewById(review1.getReviewId());
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("addLikeToReview должен увеличить рейтинг отзыва")
    void shouldAddLikeToReview() {
        reviewController.addLikeToReview(review1.getReviewId(), user2.getId());

        Optional<Review> updated = reviewRepository.getReviewById(review1.getReviewId());
        assertNotNull(updated);

    }

    @Test
    @DisplayName("deleteLikeFromReview должен уменьшить рейтинг отзыва")
    void shouldDeleteLikeFromReview() {
        reviewController.deleteLikeFromReview(review1.getReviewId(), user1.getId());

        Optional<Review> updated = reviewRepository.getReviewById(review1.getReviewId());
        assertNotNull(updated);

    }
}
