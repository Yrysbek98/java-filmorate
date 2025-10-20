package ru.yandex.practicum.filmorate.repository.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    Review createReview(Review review);

    Optional<Review> updateReview(Review review);

    void deleteReview(int id);

    Optional<Review> getReviewById(int id);

    List<Review> getAllReviews(Integer filmId, Integer count);

    void addLikeToReview(int reviewId, int userId);

    void addDislikeToReview(int reviewId, int userId);

    void deleteLikeToReview(int reviewId, int userId);

    void deleteDislikeToReview(int reviewId, int userId);
}
