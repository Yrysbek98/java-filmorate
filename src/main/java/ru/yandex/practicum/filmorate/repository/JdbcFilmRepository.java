package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;


import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcFilmRepository implements FilmRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final MpaRepository mpaRepository = new JdbcMpaRepository(jdbc);
    private final GenreRepository genreRepository = new JdbcGenreRepository(jdbc);

    @Override
    public Optional<Film> getFilmById(int id) {
        String query = """
                SELECT f.film_id, f.name, f.description, f.release_date, f.duration, 
                       m.mpa_id, m.mpa_name,
                       g.genre_id, g.genre_name
                FROM FILMS f 
                JOIN MPA m ON f.mpa_id = m.mpa_id
                LEFT JOIN FILM_GENRES fg ON f.film_id = fg.film_id
                LEFT JOIN GENRES g ON fg.genre_id = g.genre_id
                WHERE f.film_id = :id
                """;

        Map<String, Object> params = Map.of("id", id);

        List<Film> films = jdbc.query(query, params, rs -> {
            Map<Integer, Film> filmMap = new HashMap<>();

            while (rs.next()) {
                int filmId = rs.getInt("film_id");
                Film film = filmMap.get(filmId);

                if (film == null) {
                    film = new Film(
                            filmId,
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getTimestamp("release_date").toLocalDateTime().toLocalDate(),
                            rs.getInt("duration"),
                            new MPA( rs.getString("mpa_name")),
                            new ArrayList<>()
                    );
                    filmMap.put(filmId, film);
                }


                int genreId = rs.getInt("genre_id");
                if (!rs.wasNull()) {
                    Genre genre = new Genre(genreId, rs.getString("genre_name"));
                    if (!film.getGenres().contains(genre)) {
                        film.getGenres().add(genre);
                    }
                }
            }

            return new ArrayList<>(filmMap.values());
        });

        return films.isEmpty() ? Optional.empty() : Optional.of(films.get(0));
    }

    @Override
    public List<Film> findAllFilms() {
        log.info("Get all films");
        String filmQuery = """
                    SELECT f.film_id, f.name, f.description, f.release_date, f.duration,
                           m.mpa_id, m.mpa_name
                    FROM FILMS f
                    JOIN MPA m ON f.mpa_id = m.mpa_id
                """;

        Map<Integer, Film> filmMap = new LinkedHashMap<>();

        jdbc.query(filmQuery, rs -> {
            int filmId = rs.getInt("film_id");
            Film film = new Film(
                    filmId,
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getTimestamp("release_date").toLocalDateTime().toLocalDate(),
                    rs.getInt("duration"),
                    new MPA(rs.getString("mpa_name")),
                    new ArrayList<>()
            );
            filmMap.put(filmId, film);
        });

        String genreQuery = "SELECT genre_id, genre_name FROM GENRES";
        Map<Integer, Genre> genreMap = new HashMap<>();

        jdbc.query(genreQuery, rs -> {
            int genreId = rs.getInt("genre_id");
            genreMap.put(genreId, new Genre(genreId, rs.getString("genre_name")));
        });

        String filmGenresQuery = "SELECT film_id, genre_id FROM FILM_GENRES";
        jdbc.query(filmGenresQuery, rs -> {
            int filmId = rs.getInt("film_id");
            int genreId = rs.getInt("genre_id");

            Film film = filmMap.get(filmId);
            Genre genre = genreMap.get(genreId);
            if (film != null && genre != null && !film.getGenres().contains(genre)) {
                film.getGenres().add(genre);
            }
        });

        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Film createFilm(Film film) {
        log.info("Create film: id={}, name={}", film.getId(), film.getName());
        int mpaId = mpaRepository.findMpaIdByName(film.getMpa_id().getName());


        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        params.addValue("mpa_id", mpaId);


        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        String insertFilmQuery = """
                    INSERT INTO FILMS(name, description, release_date, duration, mpa_id)
                    VALUES(:name, :description, :release_date, :duration, :mpa_id)
                """;

        jdbc.update(insertFilmQuery, params, keyHolder, new String[]{"film_id"});
        int filmId = keyHolder.getKeyAs(Integer.class);


        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String insertGenreQuery = """
                        INSERT INTO FILM_GENRES(film_id, genre_id)
                        VALUES(:filmId, :genreId)
                    """;

            List<Map<String, Object>> batchParams = film.getGenres().stream()
                    .map(genre -> Map.<String, Object>of(
                            "filmId", filmId,
                            "genreId", genreRepository.findGenreIdByName(genre.getName())
                    ))
                    .toList();

            jdbc.batchUpdate(insertGenreQuery, batchParams.toArray(new Map[0]));
        }


        film.setId(filmId);
        return film;
    }

    @Override
    public Film changeFilm(Film film) {

        int mpaId = mpaRepository.findMpaIdByName(film.getMpa_id().getName());
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", film.getId());
        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        params.addValue("mpa_id", mpaId);


        String updateFilmQuery = """
                    UPDATE FILMS
                    SET name = :name,
                        description = :description,
                        release_date = :release_date,
                        duration = :duration,
                        mpa_id = :mpa_id
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

        return film;
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
                "filmId", id,
                "userId", userId
        );

        jdbc.update(deleteLikeQuery, params);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String filmQuery = """
                SELECT f.film_id, f.name, f.description, f.release_date, f.duration,
                       m.mpa_id, m.mpa_name,
                       COUNT(l.user_id) as likes_count
                FROM FILMS f
                JOIN MPA m ON f.mpa_id = m.mpa_id
                LEFT JOIN LIKES l ON f.film_id = l.film_id
                GROUP BY f.film_id, m.mpa_id, m.mpa_name
                ORDER BY likes_count DESC
                LIMIT :count
                """;

        Map<Integer, Film> filmMap = new LinkedHashMap<>();

        jdbc.query(filmQuery, Map.of("count", count), rs -> {
            int filmId = rs.getInt("film_id");
            Film film = new Film(
                    filmId,
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getTimestamp("release_date").toLocalDateTime().toLocalDate(),
                    rs.getInt("duration"),
                    new MPA(rs.getInt("mpa_id"), rs.getString("mpa_name")),
                    new ArrayList<>()
            );
            filmMap.put(filmId, film);
        });


        if (filmMap.isEmpty()) {
            return new ArrayList<>();
        }

        String genreQuery = "SELECT genre_id, genre_name FROM GENRES";
        Map<Integer, Genre> genreMap = new HashMap<>();
        jdbc.query(genreQuery, rs -> {
            int genreId = rs.getInt("genre_id");
            genreMap.put(genreId, new Genre(genreId, rs.getString("genre_name")));
        });


        String filmGenresQuery = """
                SELECT fg.film_id, fg.genre_id
                FROM FILM_GENRES fg
                WHERE fg.film_id IN (:filmIds)
                """;

        List<Integer> filmIds = new ArrayList<>(filmMap.keySet());

        jdbc.query(filmGenresQuery, Map.of("filmIds", filmIds), rs -> {
            int filmId = rs.getInt("film_id");
            int genreId = rs.getInt("genre_id");
            Film film = filmMap.get(filmId);
            Genre genre = genreMap.get(genreId);
            if (film != null && genre != null && !film.getGenres().contains(genre)) {
                film.getGenres().add(genre);
            }
        });

        return new ArrayList<>(filmMap.values());

    }


}
