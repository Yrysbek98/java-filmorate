package ru.yandex.practicum.filmorate.repository;


import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;


import java.util.*;

@Slf4j
@Repository
public class JdbcFilmRepository implements FilmRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final GenreRepository genreRepository;

    public JdbcFilmRepository(NamedParameterJdbcOperations jdbc,
                              GenreRepository genreRepository) {
        this.jdbc = jdbc;
        this.genreRepository = genreRepository;
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        String query = """
                SELECT f.film_id, f.name, f.description, f.release_date, f.duration
                FROM FILMS f
                WHERE f.film_id = :id
                """;

        List<Film> films = jdbc.query(query, Map.of("id", id), (rs, rowNum) -> new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration")
        ));

        return films.stream().findFirst();
    }

    @Override
    public Film getFilmByIdWithGenre(int id) {
        String filmQuery = """
                    SELECT f.film_id, f.name, f.description, f.release_date, f.duration,
                           m.mpa_id, m.name AS mpa_name
                    FROM FILMS f
                    LEFT JOIN MPA m ON f.mpa_id = m.mpa_id
                    WHERE f.film_id = :id
                """;

        Map<String, Object> params = Map.of("id", id);

        Film film = jdbc.queryForObject(filmQuery, params, (rs, rowNum) ->
                new Film(
                        rs.getInt("film_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("release_date").toLocalDate(),
                        rs.getInt("duration"),
                        rs.getObject("mpa_id") != null
                                ? new MPA(rs.getInt("mpa_id"), rs.getString("mpa_name"))
                                : null,
                        new ArrayList<>()
                )
        );

        if (film == null) {
            throw new FilmNotFoundException("Фильм не найден");
        }

        String genreQuery = """
                    SELECT DISTINCT g.genre_id, g.genre_name
                    FROM GENRES g
                    JOIN FILM_GENRES fg ON g.genre_id = fg.genre_id
                    WHERE fg.film_id = :filmId
                """;

        List<Genre> genres = jdbc.query(genreQuery, Map.of("filmId", id), (rs, rowNum) ->
                new Genre(rs.getInt("genre_id"), rs.getString("genre_name"))
        );

        film.setGenres(genres != null ? genres : new ArrayList<>());

        return film;
    }

    @Override
    public List<Film> findAllFilms() {
        String filmQuery = """
                SELECT f.film_id AS id, f.name, f.description, f.release_date, f.duration
                FROM FILMS f
                """;

        Map<Integer, Film> filmMap = new LinkedHashMap<>();

        jdbc.query(filmQuery, rs -> {
            int filmId = rs.getInt("id");
            Film film = new Film(
                    filmId,
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"),
                    null,
                    new ArrayList<>()
            );
            filmMap.put(filmId, film);
        });

        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Film createFilm(Film film) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", java.sql.Date.valueOf(film.getReleaseDate()));
        params.addValue("duration", film.getDuration());

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String insertFilmQuery = """
                    INSERT INTO FILMS(name, description, release_date, duration)
                    VALUES(:name, :description, :release_date, :duration)
                """;

        jdbc.update(insertFilmQuery, params, keyHolder, new String[]{"film_id"});
        int filmId = keyHolder.getKeyAs(Integer.class);
        film.setId(filmId);


        if (film.getMpa() != null && film.getMpa().getId() != 0) {
            String updateMpaQuery = "UPDATE FILMS SET mpa_id = :mpaId WHERE film_id = :filmId";
            MapSqlParameterSource mpaParams = new MapSqlParameterSource();
            mpaParams.addValue("mpaId", film.getMpa().getId());
            mpaParams.addValue("filmId", filmId);
            jdbc.update(updateMpaQuery, mpaParams);
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Genre> uniqueGenres = film.getGenres().stream()
                    .filter(g -> g != null)
                    .distinct()
                    .toList();

            for (Genre genre : uniqueGenres) {
                String insertGenreQuery = """
                        INSERT INTO FILM_GENRES(film_id, genre_id)
                        VALUES(:filmId, :genreId)
                        """;
                MapSqlParameterSource genreParams = new MapSqlParameterSource();
                genreParams.addValue("filmId", filmId);
                genreParams.addValue("genreId", genre.getId());
                jdbc.update(insertGenreQuery, genreParams);
            }
            film.setGenres(uniqueGenres);
        } else {
            film.setGenres(new ArrayList<>());
        }

        return film;

    }

    @Override
    public Optional<Film> changeFilm(Film film) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", film.getId());
        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", film.getReleaseDate());
        params.addValue("duration", film.getDuration());

        String updateFilmQuery = """
                    UPDATE FILMS
                    SET name = :name,
                        description = :description,
                        release_date = :release_date,
                        duration = :duration
                    WHERE film_id = :id
                """;

        jdbc.update(updateFilmQuery, params);

        jdbc.update("DELETE FROM FILM_GENRES WHERE film_id = :filmId",
                Map.of("filmId", film.getId()));

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String insertGenreQuery = """
                        INSERT INTO FILM_GENRES(film_id, genre_id)
                        VALUES(:filmId, :genreId)
                    """;

            List<Map<String, Object>> batchParams = film.getGenres().stream()
                    .map(genre -> Map.<String, Object>of(
                            "filmId", film.getId(),
                            "genreId", genreRepository.findGenreIdByName(genre.getName())
                    ))
                    .toList();

            jdbc.batchUpdate(insertGenreQuery, batchParams.toArray(new Map[0]));
        }

        return getFilmById(film.getId());

    }

    @Override
    public void addLike(int id, int userId) {
        String addLikeQuery = """
                INSERT INTO LIKES (film_id, user_id)
                VALUES (:filmId, :userId)
                """;

        Map<String, Object> params = Map.of(
                "filmId", id,
                "userId", userId
        );

        jdbc.update(addLikeQuery, params);
    }

    @Override
    public void deleteLike(int id, int userId) {
        String deleteLikeQuery = """
                DELETE FROM LIKES
                WHERE film_id = :id AND user_id = :userId
                """;

        Map<String, Object> params = Map.of(
                "id", id,
                "userId", userId
        );

        jdbc.update(deleteLikeQuery, params);
    }

    @Override
    public List<Film> getPopularFilms(int count) {

        String filmQuery = """
                SELECT f.film_id AS id, f.name, f.description, f.release_date, f.duration,
                       COUNT(l.user_id) AS likes_count
                FROM FILMS f
                LEFT JOIN LIKES l ON f.film_id = l.film_id
                GROUP BY f.film_id
                ORDER BY likes_count DESC
                LIMIT :count
                """;

        List<Film> films = new ArrayList<>();

        jdbc.query(filmQuery, Map.of("count", count), rs -> {
            Film film = new Film(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"),
                    null,
                    new ArrayList<>()
            );
            films.add(film);
        });

        return films;
    }
}
