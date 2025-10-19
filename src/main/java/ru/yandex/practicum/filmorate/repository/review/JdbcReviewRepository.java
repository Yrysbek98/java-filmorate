package ru.yandex.practicum.filmorate.repository.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class JdbcReviewRepository implements ReviewRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Review createReview(Review review) {
        String sql = """
                    INSERT INTO REVIEWS (content, is_positive, useful, user_id, film_id)
                    VALUES (:content, :is_positive, :useful, :userId, :filmId)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("content", review.getContent())
                .addValue("is_positive", review.getIsPositive())
                .addValue("useful", 0)
                .addValue("userId", review.getUserId())
                .addValue("filmId", review.getFilmId());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"review_id"});
        int id = keyHolder.getKey().intValue();

        return getReviewById(id).get();
    }

    @Override
    public Optional<Review> updateReview(Review review) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("content", review.getContent());
        params.addValue("is_positive", review.getIsPositive());
        params.addValue("useful", review.getUseful());
        params.addValue("user_id", review.getUserId());
        params.addValue("film_id", review.getFilmId());
        params.addValue("id", review.getReviewId());
        String updateReview = """
                UPDATE REVIEWS
                SET CONTENT = :content, IS_POSITIVE = :is_positive, USEFUL = :useful, USER_ID = :user_id, FILM_ID = :film_id
                WHERE review_id = :id
                """;
        jdbc.update(updateReview, params);
        return getReviewById(review.getReviewId());
    }

    @Override
    public void deleteReview(int id) {
        String deleteReview = """
                DELETE FROM REVIEWS
                WHERE review_id = :reviewId
                """;
        Map<String, Object> params = Map.of(
                "reviewId", id
        );
        jdbc.update(deleteReview, params);
    }

    @Override
    public Optional<Review> getReviewById(int id) {
        String query = """
                SELECT review_id, content, is_positive, useful, user_id, film_id
                FROM REVIEWS
                WHERE review_id = :id
                """;
        Map<String, Object> params = Map.of("id", id);
        List<Review> reviews = jdbc.query(query, params, ((rs, rowNum) ->
                new Review(
                        rs.getObject("review_id", Integer.class),
                        rs.getString("content"),
                        rs.getObject("is_positive", Boolean.class),
                        rs.getInt("useful"),
                        rs.getObject("user_id", Integer.class),
                        rs.getObject("film_id", Integer.class)
                )));
        return reviews.isEmpty() ? Optional.empty() : Optional.of(reviews.get(0));
    }

    @Override
    public List<Review> getAllReviews(Integer filmId, Integer count) {
        String baseSql = """
                SELECT review_id, content, is_positive, useful, user_id, film_id
                FROM REVIEWS
                """;
        String sqlWithFilmId;
        Map<String, Object> params = new HashMap<>();
        if (filmId != null && filmId > 0) {
            sqlWithFilmId = baseSql + "\nWHERE film_id = :filmId";
            params.put("filmId", filmId);
        } else {
            sqlWithFilmId = baseSql;
        }

        List<Review> reviews = new ArrayList<>();
        jdbc.query(sqlWithFilmId, params, (ResultSet rs) -> {
                    Review review = new Review(
                            rs.getObject("review_id", Integer.class),
                            rs.getString("content"),
                            rs.getObject("is_positive", Boolean.class),
                            rs.getInt("useful"),
                            rs.getObject("user_id", Integer.class),
                            rs.getObject("film_id", Integer.class));

                    reviews.add(review);
                }

        );
        return reviews;
    }

    @Override
    public void addLikeToReview(int reviewId, int userId) {
        Optional<Review> reviewOpt = getReviewById(reviewId);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", reviewId);
        String updateReview = """
                UPDATE REVIEWS
                SET  USEFUL = USEFUL + 1
                WHERE review_id = :id
                """;
        jdbc.update(updateReview, params);


    }

    @Override
    public Optional<Review> addDislikeToReview(int reviewId, int userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", reviewId);

        String updateReview = """
                UPDATE REVIEWS
                   SET USEFUL = CASE WHEN USEFUL = 1 THEN USEFUL - 2 ELSE USEFUL - 1 END
                WHERE review_id = :id
                """;
        jdbc.update(updateReview, params);

        return getReviewById(reviewId);
    }

    @Override
    public void deleteLikeToReview(int reviewId, int userId) {
        Optional<Review> reviewOpt = getReviewById(reviewId);
        Review review = reviewOpt.get();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", reviewId);
        String updateReview = """
                UPDATE REVIEWS
                SET USEFUL = USEFUL - 1
                WHERE review_id = :id
                """;
        jdbc.update(updateReview, params);

    }

    @Override
    public void deleteDislikeToReview(int reviewId, int userId) {
        Optional<Review> reviewOpt = getReviewById(reviewId);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", reviewId);
        String updateReview = """
                UPDATE REVIEWS
                SET  USEFUL = USEFUL + 1
                WHERE review_id = :id
                """;
        jdbc.update(updateReview, params);


    }
}
