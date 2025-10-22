package ru.yandex.practicum.filmorate.serviceDB.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.film.FilmValidationException;
import ru.yandex.practicum.filmorate.exception.review.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.review.ReviewValidationException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.repository.event.EventRepository;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.review.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasedReviewService implements ReviewServiceDB {
    final ReviewRepository reviewRepository;
    final FilmRepository filmRepository;
    final UserRepository userRepository;
    final EventRepository eventRepository;

    @Override
    public Review createReview(Review review) {
        if (userRepository.getUserById(review.getUserId()).isEmpty()) {
            throw new UserNotFoundException("Пользователь с id=" + review.getUserId() + " не найден");
        }
        if (review.getUserId() == null) {
            throw new UserNotFoundException("Пользователь не может быть null");
        }
        if (filmRepository.getFilmById(review.getFilmId()).isEmpty()) {
            throw new FilmNotFoundException("Фильм с id=" + review.getFilmId() + " не найден");
        }
        if (review.getFilmId() == null) {
            throw new FilmNotFoundException("Фильм не может быть null");
        }

        Review savedReview = reviewRepository.createReview(review);

        Event event = new Event(
                System.currentTimeMillis(),
                savedReview.getUserId(),
                EventType.REVIEW,
                Operation.ADD,
                savedReview.getReviewId());
        eventRepository.addEvent(event);

        return savedReview;
    }

    @Override
    public Review updateReview(Review review) {
        Optional<Review> r = reviewRepository.getReviewById(review.getReviewId());
        if (r.isEmpty()) {
            throw new ReviewNotFoundException("Отзыв с таким id=" + review.getReviewId() + " не  найден");
        }

        Event event = new Event(
                System.currentTimeMillis(),
                r.get().getUserId(),
                EventType.REVIEW,
                Operation.UPDATE,
                r.get().getReviewId());
        eventRepository.addEvent(event);
        return reviewRepository.updateReview(review);
    }

    @Override
    public void deleteReview(int id) {
        Optional<Review> r = reviewRepository.getReviewById(id);
        if (r.isEmpty()) {
            throw new ReviewNotFoundException("Отзыв с таким id=" + id + " не  найден");
        }

        Event event = new Event(
                System.currentTimeMillis(),
                r.get().getUserId(),
                EventType.REVIEW,
                Operation.REMOVE,
                r.get().getReviewId());
        eventRepository.addEvent(event);

        reviewRepository.deleteReview(id);
    }

    @Override
    public Review getReviewById(int id) {
        Optional<Review> r = reviewRepository.getReviewById(id);
        if (r.isEmpty()) {
            throw new ReviewNotFoundException("Отзыв не найден");
        }

        return reviewRepository.getReviewById(id).get();
    }


    @Override
    public List<Review> getAllReviews(Integer filmId, Integer count) {

        if (filmId != null && filmId <= 0) {
            throw new FilmValidationException("Неправильно указан id фильма");
        }

        if (filmId != null && filmRepository.getFilmById(filmId).isEmpty()) {
            throw new FilmNotFoundException("Фильм с id=" + filmId + " не найден");
        }

        if (count != null && count <= 0) {
            throw new ReviewValidationException("Неправильно указано количество отзывов");
        }
        return reviewRepository.getAllReviews(filmId, count);
    }

    @Override
    public void addLikeToReview(int reviewId, int userId) {
        Optional<Review> r = reviewRepository.getReviewById(reviewId);
        if (r.isEmpty()) {
            throw new ReviewNotFoundException("Отзыв не найден");
        }
        Optional<User> u = userRepository.getUserById(userId);
        if (u.isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        reviewRepository.addLikeToReview(reviewId, userId);
    }

    @Override
    public void addDislikeToReview(int reviewId, int userId) {
        Optional<Review> r = reviewRepository.getReviewById(reviewId);
        if (r.isEmpty()) {
            throw new ReviewNotFoundException("Отзыв не найден");
        }
        Optional<User> u = userRepository.getUserById(userId);
        if (u.isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        reviewRepository.addDislikeToReview(reviewId, userId);
    }

    @Override
    public void deleteLikeToReview(int reviewId, int userId) {
        Optional<Review> r = reviewRepository.getReviewById(reviewId);
        if (r.isEmpty()) {
            throw new ReviewNotFoundException("Отзыв не найден");
        }
        Optional<User> u = userRepository.getUserById(userId);
        if (u.isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        reviewRepository.deleteLikeToReview(reviewId, userId);
    }

    @Override
    public void deleteDislikeToReview(int reviewId, int userId) {
        Optional<Review> r = reviewRepository.getReviewById(reviewId);
        if (r.isEmpty()) {
            throw new ReviewNotFoundException("Отзыв не найден");
        }
        Optional<User> u = userRepository.getUserById(userId);
        if (u.isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден");
        }

        reviewRepository.deleteDislikeToReview(reviewId, userId);
    }
}
