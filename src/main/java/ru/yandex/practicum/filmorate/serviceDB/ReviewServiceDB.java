package ru.yandex.practicum.filmorate.serviceDB;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewServiceDB {
    Review createReview(Review review);

    Optional<Review> updateReview(Review review);

    void deleteReview(int id);

    Optional<Review> getReviewById(int id);

    List<Review> getAllReviews(Integer filmId, Integer count);

    void addLikeToReview(int review_id, int user_id);

    Optional<Review> addDislikeToReview(int review_id, int user_id);

    void deleteLikeToReview(int review_id, int user_id);

    void deleteDislikeToReview(int review_id, int user_id);
}
