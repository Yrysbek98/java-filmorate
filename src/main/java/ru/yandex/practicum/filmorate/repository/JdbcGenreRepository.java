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


        List<Genre> genres =  jdbc.query(query, params, (rs, rowNum) ->
                new Genre(
                        rs.getInt("genre_id"),
                        rs.getString("genre_name")
                ));
        return genres.stream().findFirst();

    }

    @Override
    public List<Genre> getAllGenres() {
        String query = """
                SELECT g.genre_id, g.genre_name
                FROM GENRES AS g
                """;
        Map<Integer, Genre> genreMap = new LinkedHashMap<>();
        jdbc.query(query, rs -> {
            int genreId = rs.getInt("genre_id");
            Genre genre = new Genre(
                    genreId,
                    rs.getString("genre_name")
            );
            genreMap.put(genreId, genre);
        });
        return new ArrayList<>(genreMap.values());
    }

}
