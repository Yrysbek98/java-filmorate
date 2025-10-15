package ru.yandex.practicum.filmorate.serviceDB;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasedReviewService implements ReviewServiceDB{
    final ReviewRepository reviewRepository;
    final FilmRepository filmRepository;
    final UserRepository userRepository;

    @Override
    public Review createReview(Review review) {
        if (userRepository.getUserById(review.getUserId()).isEmpty()) {
            throw new UserNotFoundException("Пользователь с id=" + review.getUserId() + " не найден");
        }
        if (review.getUserId() == null ){
            throw new UserNotFoundException("Пользователь не может быть null");
        }
        if (filmRepository.getFilmById(review.getFilmId()).isEmpty()) {
            throw new FilmNotFoundException("Фильм с id=" + review.getFilmId() + " не найден");
        }
        if (review.getFilmId() == null ){
            throw new FilmNotFoundException("Фильм не может быть null");
        }

        return reviewRepository.createReview(review);

    }

    @Override
    public Optional<Review> updateReview(Review review) {
        Optional<Review> r = reviewRepository.getReviewById(review.getReviewId());
        if (r.isEmpty()){
            throw  new ReviewNotFoundException("Отзыв с таким id=" + review.getReviewId() + " не  найден");
        }
        return reviewRepository.updateReview(review);
    }

    @Override
    public void deleteReview(int id) {
        Optional<Review> r = reviewRepository.getReviewById(id);
        if (r.isEmpty()){
            throw  new ReviewNotFoundException("Отзыв с таким id=" + id + " не  найден");
        }
        reviewRepository.deleteReview(id);
    }

    @Override
    public Optional<Review> getReviewById(int id) {
        Optional<Review> r = reviewRepository.getReviewById(id);
        if (r.isEmpty()) {
            throw new ReviewNotFoundException("Отзыв не найден");
        }
        return reviewRepository.getReviewById(id);
    }


    @Override
    public List<Review> getAllReviews(Integer filmId, Integer count) {
        if (filmId <= 0) {
            throw new FilmValidationException("Неправильно указан id фильма");
        }
        if (filmRepository.getFilmById(filmId).isEmpty()) {
            throw new FilmNotFoundException("Фильм с id=" + filmId + " не найден");
        }
        if (count <= 0) {
            throw new ReviewValidationException("Неправильно указано количество отзывов");
        }
        return reviewRepository.getAllReviews(filmId, count);
    }

    @Override
    public void addLikeToReview(int review_id, int user_id) {
        Optional<Review> r = reviewRepository.getReviewById(review_id);
        if (r.isEmpty()) {
            throw new ReviewNotFoundException("Отзыв не найден");
        }
        Optional<User> u = userRepository.getUserById(user_id);
        if (u.isEmpty()){
            throw new UserNotFoundException("Пользователь не найден");
        }
        reviewRepository.addLikeToReview(review_id,user_id);
    }

    @Override
    public Optional<Review> addDislikeToReview(int review_id, int user_id) {
        Optional<Review> r = reviewRepository.getReviewById(review_id);
        if (r.isEmpty()) {
            throw new ReviewNotFoundException("Отзыв не найден");
        }
        Optional<User> u = userRepository.getUserById(user_id);
        if (u.isEmpty()){
            throw new UserNotFoundException("Пользователь не найден");
        }
        return reviewRepository.addDislikeToReview(review_id,user_id);
    }

    @Override
    public void deleteLikeToReview(int review_id, int user_id) {
        Optional<Review> r = reviewRepository.getReviewById(review_id);
        if (r.isEmpty()) {
            throw new ReviewNotFoundException("Отзыв не найден");
        }
        Optional<User> u = userRepository.getUserById(user_id);
        if (u.isEmpty()){
            throw new UserNotFoundException("Пользователь не найден");
        }
        reviewRepository.deleteLikeToReview(review_id, user_id);
    }

    @Override
    public void deleteDislikeToReview(int review_id, int user_id) {
        Optional<Review> r = reviewRepository.getReviewById(review_id);
        if (r.isEmpty()) {
            throw new ReviewNotFoundException("Отзыв не найден");
        }
        Optional<User> u = userRepository.getUserById(user_id);
        if (u.isEmpty()){
            throw new UserNotFoundException("Пользователь не найден");
        }

        reviewRepository.deleteDislikeToReview(review_id,user_id );
    }
}
