package ru.yandex.practicum.filmorate.repository;


import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;


import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class JdbcFilmRepository implements FilmRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final GenreRepository genreRepository;
    private final DirectorRepository directorRepository;

    public JdbcFilmRepository(NamedParameterJdbcOperations jdbc,
                              GenreRepository genreRepository, DirectorRepository directorRepository) {
        this.jdbc = jdbc;
        this.genreRepository = genreRepository;
        this.directorRepository = directorRepository;
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        String query = """
                SELECT f.film_id, f.name, f.description, f.release_date, f.duration,
                       m.mpa_id, m.name AS mpa_name
                FROM FILMS f
                LEFT JOIN MPA m ON f.mpa_id = m.mpa_id
                WHERE f.film_id = :id
                """;

        Map<String, Object> params = Map.of("id", id);

        try {
            Film film = jdbc.queryForObject(query, params, (rs, rowNum) ->
                    new Film(
                            rs.getInt("film_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDate("release_date").toLocalDate(),
                            rs.getInt("duration"),
                            rs.getObject("mpa_id") != null
                                    ? new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"))
                                    : null,
                            new ArrayList<>()
                    )
            );

            if (film != null) {
                // Загружаем жанры для фильма
                String genreQuery = """
                        SELECT DISTINCT g.genre_id, g.genre_name
                        FROM GENRES g
                        JOIN FILM_GENRES fg ON g.genre_id = fg.genre_id
                        WHERE fg.film_id = :filmId
                        """;

                List<Genre> genres = jdbc.query(genreQuery, Map.of("filmId", id), (rs2, rowNum2) ->
                        new Genre(rs2.getInt("genre_id"), rs2.getString("genre_name"))
                );

                film.setGenres(genres != null ? genres : new ArrayList<>());

                loadDirectorsForFilm(film);
            }

            return Optional.ofNullable(film);
        } catch (Exception e) {
            return Optional.empty();
        }
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
                                ? new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"))
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

        loadDirectorsForFilm(film);

        return film;
    }

    @Override
    public List<Film> findAllFilms() {
        String filmQuery = """
                SELECT f.film_id AS id, f.name, f.description, f.release_date, f.duration,
                       m.mpa_id, m.name AS mpa_name
                FROM FILMS f
                LEFT JOIN MPA m ON f.mpa_id = m.mpa_id
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
                    rs.getObject("mpa_id") != null
                            ? new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"))
                            : null,
                    new ArrayList<>()
            );
            filmMap.put(filmId, film);
        });

        List<Film> filmList = new ArrayList<>(filmMap.values());

        loadGenresForFilmList(filmList);
        loadDirectorsForFilms(filmList);

        return filmList;
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
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

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

        directorRepository.updateDirectorsForFilm(film);

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

        // Обновляем MPA рейтинг если он есть
        if (film.getMpa() != null && film.getMpa().getId() != 0) {
            String updateMpaQuery = "UPDATE FILMS SET mpa_id = :mpaId WHERE film_id = :filmId";
            MapSqlParameterSource mpaParams = new MapSqlParameterSource();
            mpaParams.addValue("mpaId", film.getMpa().getId());
            mpaParams.addValue("filmId", film.getId());
            jdbc.update(updateMpaQuery, mpaParams);
        }

        // Обновляем жанры
        jdbc.update("DELETE FROM FILM_GENRES WHERE film_id = :filmId",
                Map.of("filmId", film.getId()));

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String insertGenreQuery = """
                    INSERT INTO FILM_GENRES(film_id, genre_id)
                    VALUES(:filmId, :genreId)
                    """;

            List<Map<String, Object>> batchParams = film.getGenres().stream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .map(genre -> Map.<String, Object>of(
                            "filmId", film.getId(),
                            "genreId", genre.getId()
                    ))
                    .collect(Collectors.toList());

            jdbc.batchUpdate(insertGenreQuery, batchParams.toArray(new Map[0]));
        }

        directorRepository.updateDirectorsForFilm(film);

        return getFilmById(film.getId());
    }

    // Удаление фильма по id: true - если удалили
    @Override
    public boolean deleteFilm(int id) {
        jdbc.update("DELETE FROM LIKES WHERE FILM_ID = :id",
                new MapSqlParameterSource("id", id));

        jdbc.update("DELETE FROM FILM_GENRES WHERE FILM_ID = :id",
                new MapSqlParameterSource("id", id));

        return jdbc.update("DELETE FROM FILMS WHERE FILM_ID = :id",
                new MapSqlParameterSource("id", id)) > 0;
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
    public List<Film> getPopularFilms(int count, Integer genreId, Integer year) {
        StringBuilder filmQuery = new StringBuilder("""
                    SELECT f.film_id AS id, f.name, f.description, f.release_date, f.duration,
                           m.mpa_id, m.name AS mpa_name,
                           COUNT(l.user_id) AS likes_count
                    FROM FILMS f
                    LEFT JOIN MPA m ON f.mpa_id = m.mpa_id
                    LEFT JOIN LIKES l ON f.film_id = l.film_id
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        // Добавляем условия фильтрации
        if (genreId != null) {
            filmQuery.append(" JOIN FILM_GENRES fg ON f.film_id = fg.film_id AND fg.genre_id = :genreId ");
            params.addValue("genreId", genreId);
        }

        if (year != null) {
            filmQuery.append(" WHERE EXTRACT(YEAR FROM f.release_date) = :year ");
            params.addValue("year", year);
        }

        filmQuery.append("""
                    GROUP BY f.film_id, m.mpa_id, m.name
                    ORDER BY likes_count DESC
                    LIMIT :count
                """);

        Map<Integer, Film> filmMap = new LinkedHashMap<>();

        jdbc.query(filmQuery.toString(), params, rs -> {
            int filmId = rs.getInt("id");
            Film film = new Film(
                    filmId,
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"),
                    rs.getObject("mpa_id") != null
                            ? new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"))
                            : null,
                    new ArrayList<>()
            );
            filmMap.put(filmId, film);
        });

        List<Film> filmList = new ArrayList<>(filmMap.values());
        loadGenresForFilmList(filmList);
        loadDirectorsForFilms(filmList);

        return filmList;
    }

    @Override
    public List<Film> getFilmsByIds(List<Integer> filmIds) {
        if (filmIds.isEmpty()) {
            return new ArrayList<>();
        }

        String filmQuery = """
                SELECT f.film_id, f.name, f.description, f.release_date, f.duration,
                       m.mpa_id, m.name AS mpa_name
                FROM FILMS f
                LEFT JOIN MPA m ON f.mpa_id = m.mpa_id
                WHERE f.film_id IN (:filmIds)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("filmIds", filmIds);

        Map<Integer, Film> filmMap = new HashMap<>();

        jdbc.query(filmQuery, params, rs -> {
            int filmId = rs.getInt("film_id");
            Film film = new Film(
                    filmId,
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"),
                    rs.getObject("mpa_id") != null
                            ? new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"))
                            : null,
                    new ArrayList<>()
            );
            filmMap.put(filmId, film);
        });

        // Сохраняем порядок из входного списка
        List<Film> resultFilms = filmIds.stream()
                .map(filmMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        loadDirectorsForFilms(resultFilms);
        loadGenresForFilmList(resultFilms);
        return resultFilms;
    }

    // Получение списка фильмов режиссера
    @Override
    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        String sql = switch (sortBy) {
            case "year" ->
                // Сортировка по году выпуска
                    """
                                SELECT f.*, m.mpa_id, m.name AS mpa_name
                                FROM FILMS f
                                JOIN film_director fd ON f.film_id = fd.film_id
                                LEFT JOIN MPA m ON f.mpa_id = m.mpa_id
                                WHERE fd.director_id = :directorId
                                ORDER BY f.release_date
                            """;
            case "likes" ->
                // Сортировка по количеству лайков
                    """
                                SELECT f.*, m.mpa_id, m.name AS mpa_name, COUNT(l.user_id) AS likes_count
                                FROM FILMS f
                                JOIN film_director fd ON f.film_id = fd.film_id
                                LEFT JOIN MPA m ON f.mpa_id = m.mpa_id
                                LEFT JOIN LIKES l ON f.film_id = l.film_id
                                WHERE fd.director_id = :directorId
                                GROUP BY f.film_id
                                ORDER BY likes_count DESC
                            """;
            default ->
                    throw new IllegalArgumentException("Неверный параметр сортировки: " + sortBy);
        };

        SqlParameterSource params = new MapSqlParameterSource("directorId", directorId);

        List<Film> films = jdbc.query(sql, params, (rs, rowNum) -> new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")),
                new ArrayList<>()
        ));

        if (films.isEmpty()) {
            return Collections.emptyList();
        }

        loadGenresForFilmList(films);
        loadDirectorsForFilms(films);

        return films;
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sql = """
            SELECT f.film_id
            FROM likes l
            JOIN films f ON l.film_id = f.film_id
            WHERE l.film_id IN (
                SELECT film_id
                FROM likes
                WHERE user_id IN (:userId, :friendId)
                GROUP BY film_id
                HAVING COUNT(DISTINCT user_id) = 2
            )
            GROUP BY f.film_id
            ORDER BY COUNT(l.user_id) DESC
            """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("friendId", friendId);

        List<Integer> filmIds = jdbc.queryForList(sql, params, Integer.class);

        if (filmIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Film> films = getFilmsByIds(filmIds);

        return films;
    }

    // Метод для загрузки режиссера фильма
    private void loadDirectorsForFilm(Film film) {
        if (film == null) return;
        film.setDirectors(directorRepository.getDirectorsByFilmId(film.getId()));
    }

    // Метод для загрузки жанров списка фильмов
    private void loadGenresForFilmList(List<Film> films) {
        if (films == null || films.isEmpty()) {
            return;
        }

        Set<Integer> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toSet());

        String sql = """
            SELECT fg.film_id, g.genre_id, g.genre_name
            FROM FILM_GENRES fg
            JOIN GENRES g ON fg.genre_id = g.genre_id
            WHERE fg.film_id IN (:filmIds)
            """;

        SqlParameterSource params = new MapSqlParameterSource("filmIds", filmIds);

        final Map<Integer, List<Genre>> genresByFilmId = new HashMap<>();

        jdbc.query(sql, params, (rs) -> {
            int filmId = rs.getInt("film_id");
            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
            genresByFilmId.computeIfAbsent(filmId, k -> new ArrayList<>()).add(genre);
        });

        for (Film film : films) {
            film.setGenres(genresByFilmId.getOrDefault(film.getId(), new ArrayList<>()));
        }
    }

    // Метод для загрузки режиссеров для всех фильмов
    private void loadDirectorsForFilms(List<Film> films) {
        if (films == null || films.isEmpty()) {
            return;
        }

        List<Integer> filmIds = films.stream().map(Film::getId).toList();
        Map<Integer, Set<Director>> directorsByFilmId = new HashMap<>();

        String query = "SELECT d.id, d.name, fd.film_id " +
                "FROM directors AS d " +
                "JOIN film_director AS fd ON d.id = fd.director_id " +
                "WHERE fd.film_id IN (:filmIds)";

        SqlParameterSource params = new MapSqlParameterSource("filmIds", filmIds);

        jdbc.query(query, params, (rs) -> {
            Director director = new Director(rs.getInt("id"), rs.getString("name"));
            int filmId = rs.getInt("film_id");
            directorsByFilmId.computeIfAbsent(filmId, k -> new LinkedHashSet<>()).add(director);
        });

        for (Film film : films) {
            film.setDirectors(directorsByFilmId.getOrDefault(film.getId(), new LinkedHashSet<>()));
        }
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        String pattern = "%" + query.toLowerCase() + "%";

        StringBuilder sql = new StringBuilder("""
                    SELECT f.film_id, f.name, f.description, f.release_date, f.duration,
                           m.mpa_id, m.name AS mpa_name,
                           COUNT(l.user_id) AS likes_count
                    FROM films f
                    LEFT JOIN mpa m ON f.mpa_id = m.mpa_id
                    LEFT JOIN likes l ON f.film_id = l.film_id
                    LEFT JOIN film_director fd ON f.film_id = fd.film_id
                    LEFT JOIN directors d ON fd.director_id = d.id
                    WHERE
                """);

        List<String> conditions = new ArrayList<>();

        if (by.contains("title")) {
            conditions.add("LOWER(f.name) LIKE :pattern");
        }
        if (by.contains("director")) {
            conditions.add("LOWER(d.name) LIKE :pattern");
        }

        if (conditions.isEmpty()) {
            throw new FilmValidationException("Параметр 'by' должен содержать хотя бы 'title' или 'director'");
        }

        sql.append(String.join(" OR ", conditions));
        sql.append("""
                    GROUP BY f.film_id
                    ORDER BY likes_count DESC
                """);

        MapSqlParameterSource params = new MapSqlParameterSource("pattern", pattern);

        Map<Integer, Film> filmMap = new LinkedHashMap<>();

        jdbc.query(sql.toString(), params, rs -> {
            int filmId = rs.getInt("film_id");
            Film film = new Film(
                    filmId,
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"),
                    rs.getObject("mpa_id") != null
                            ? new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"))
                            : null,
                    new ArrayList<>()
            );
            filmMap.put(filmId, film);
        });

        if (!filmMap.isEmpty()) {
            String directorQuery = """
                        SELECT fd.film_id, d.id AS director_id, d.name AS director_name
                        FROM film_director fd
                        JOIN directors d ON fd.director_id = d.id
                        WHERE fd.film_id IN (:filmIds)
                    """;

            MapSqlParameterSource directorParams = new MapSqlParameterSource();
            directorParams.addValue("filmIds", new ArrayList<>(filmMap.keySet()));

            jdbc.query(directorQuery, directorParams, rs -> {
                int filmId = rs.getInt("film_id");
                Film film = filmMap.get(filmId);
                if (film != null) {
                    film.getDirectors().add(new Director(
                            rs.getInt("director_id"),
                            rs.getString("director_name")
                    ));
                }
            });
        }

        if (!filmMap.isEmpty()) {
            String genreQuery = """
            SELECT fg.film_id, g.genre_id, g.genre_name
            FROM film_genres fg
            JOIN genres g ON fg.genre_id = g.genre_id
            WHERE fg.film_id IN (:filmIds)
        """;

            MapSqlParameterSource genreParams = new MapSqlParameterSource();
            genreParams.addValue("filmIds", new ArrayList<>(filmMap.keySet()));

            jdbc.query(genreQuery, genreParams, rs -> {
                int filmId = rs.getInt("film_id");
                Film film = filmMap.get(filmId);
                if (film != null) {
                    film.getGenres().add(new Genre(
                            rs.getInt("genre_id"),
                            rs.getString("genre_name")
                    ));
                }
            });
        }


        return new ArrayList<>(filmMap.values());
    }
}