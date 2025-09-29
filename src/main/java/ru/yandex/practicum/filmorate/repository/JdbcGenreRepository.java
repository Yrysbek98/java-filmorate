package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;


import java.util.*;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public int findGenreIdByName(String genreName) {
        String query = "SELECT genre_id FROM GENRES WHERE genre_name = :name";
        Map<String, Object> params = Map.of("name", genreName);


        return jdbc.queryForObject(query, params, Integer.class);
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        String query = """
                SELECT genre_id, genre_name
                FROM GENRES
                WHERE  genre_id = :id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);


        return Optional.ofNullable(jdbc.queryForObject(query, params, (rs, rowNum) -> {
            int genreId = rs.getInt("genre_id");
            String genreName = rs.getString("genre_name");
            return new Genre(genreId, genreName);
        }));

    }

    @Override
    public List<Genre> getAllGenres() {
        String query = "SELECT genre_id, genre_name  FROM GENRES";
        Map<Integer, Genre> genreMap = new LinkedHashMap<>();
        jdbc.query(query, rs -> {
            int genreId = rs.getInt("user_id");
            Genre genre = new Genre(
                    rs.getInt("id"),
                    rs.getString("name")
            );
            genreMap.put(genreId, genre);
        });
        return new ArrayList<>(genreMap.values());
    }


}
