package ru.yandex.practicum.filmorate.serviceDB.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;


public interface ReviewServiceDB {
    Review createReview(Review review);

    Review updateReview(Review review);

    void deleteReview(int id);

    Review getReviewById(int id);

    List<Review> getAllReviews(Integer filmId, Integer count);

    void addLikeToReview(int reviewId, int userId);

    void addDislikeToReview(int reviewId, int userId);

    void deleteLikeToReview(int reviewId, int userId);

    void deleteDislikeToReview(int reviewId, int userId);
}
