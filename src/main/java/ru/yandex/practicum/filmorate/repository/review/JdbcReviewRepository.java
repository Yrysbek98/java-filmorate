package ru.yandex.practicum.filmorate.repository.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class JdbcReviewRepository implements ReviewRepository {
    private final NamedParameterJdbcOperations jdbc;


    @Override
    public Review createReview(Review review) {
        String checkSql = """
                SELECT COUNT(*)
                FROM REVIEWS
                WHERE USER_ID = :userId AND FILM_ID = :filmId
                """;

        Map<String, Object> param = Map.of(
                "userId", review.getUserId(),
                "filmId", review.getFilmId()
        );

        Boolean exists = jdbc.queryForObject(checkSql, param, Boolean.class);
        if (exists != null && exists) {
            throw new IllegalArgumentException("Пользователь уже оставил отзыв на этот фильм");
        }

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

        return new Review(
                id,
                review.getContent(),
                review.getIsPositive(),
                0,
                review.getUserId(),
                review.getFilmId()
        );
    }


    @Override
    public Optional<Review> updateReview(Review review) {
        Optional<Review> existingReview = getReviewById(review.getReviewId());
        if (existingReview.isEmpty()) {
            return Optional.empty();
        }


        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("content", review.getContent());
        params.addValue("is_positive", review.getIsPositive());
        params.addValue("useful", review.getUseful());
        params.addValue("id", review.getReviewId());

        String updateReview = """
                UPDATE REVIEWS
                SET CONTENT = :content, IS_POSITIVE = :is_positive, USEFUL = :useful
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

        StringBuilder sqlBuilder = new StringBuilder("""
                SELECT review_id, content, is_positive, useful, user_id, film_id
                FROM REVIEWS
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (filmId != null && filmId > 0) {
            sqlBuilder.append("WHERE film_id = :filmId\n");
            params.addValue("filmId", filmId);
        }

        sqlBuilder.append("ORDER BY useful DESC\n");

        if (count != null && count > 0) {
            sqlBuilder.append("LIMIT :count");
            params.addValue("count", count);
        }

        return jdbc.query(sqlBuilder.toString(), params, (rs, rowNum) ->
                new Review(
                        rs.getObject("review_id", Integer.class),
                        rs.getString("content"),
                        rs.getObject("is_positive", Boolean.class),
                        rs.getInt("useful"),
                        rs.getObject("user_id", Integer.class),
                        rs.getObject("film_id", Integer.class)
                )
        );
    }

    @Override
    public void addLikeToReview(int reviewId, int userId) {
        String deleteSql = "DELETE FROM REVIEW_LIKES WHERE review_id = :reviewId AND user_id = :userId";
        MapSqlParameterSource deleteParams = new MapSqlParameterSource()
                .addValue("reviewId", reviewId)
                .addValue("userId", userId);
        jdbc.update(deleteSql, deleteParams);

        String insertSql = "INSERT INTO REVIEW_LIKES (review_id, user_id, is_like) VALUES (:reviewId, :userId, true)";
        jdbc.update(insertSql, deleteParams);

        recalculateUsefulness(reviewId);
    }

    @Override
    public void addDislikeToReview(int reviewId, int userId) {
        String deleteSql = "DELETE FROM REVIEW_LIKES WHERE review_id = :reviewId AND user_id = :userId";
        MapSqlParameterSource deleteParams = new MapSqlParameterSource()
                .addValue("reviewId", reviewId)
                .addValue("userId", userId);
        jdbc.update(deleteSql, deleteParams);

        String insertSql = "INSERT INTO REVIEW_LIKES (review_id, user_id, is_like) VALUES (:reviewId, :userId, false)";
        jdbc.update(insertSql, deleteParams);

        recalculateUsefulness(reviewId);
    }

    @Override
    public void deleteLikeToReview(int reviewId, int userId) {
        String deleteSql = "DELETE FROM REVIEW_LIKES WHERE review_id = :reviewId AND user_id = :userId AND is_like = true";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("reviewId", reviewId)
                .addValue("userId", userId);
        jdbc.update(deleteSql, params);

        recalculateUsefulness(reviewId);

    }

    @Override
    public void deleteDislikeToReview(int reviewId, int userId) {
        String deleteSql = "DELETE FROM REVIEW_LIKES WHERE review_id = :reviewId AND user_id = :userId AND is_like = false";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("reviewId", reviewId)
                .addValue("userId", userId);
        jdbc.update(deleteSql, params);

        recalculateUsefulness(reviewId);


    }

    private void recalculateUsefulness(int reviewId) {
        String calculateSql = """
                UPDATE REVIEWS
                SET USEFUL = (
                    SELECT COUNT(CASE WHEN is_like THEN 1 END) - COUNT(CASE WHEN NOT is_like THEN 1 END)
                    FROM REVIEW_LIKES
                    WHERE review_id = :reviewId
                )
                WHERE review_id = :reviewId
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("reviewId", reviewId);
        jdbc.update(calculateSql, params);
    }
}
