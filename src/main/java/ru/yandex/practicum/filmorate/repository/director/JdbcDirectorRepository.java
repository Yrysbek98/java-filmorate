package ru.yandex.practicum.filmorate.repository.director;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JdbcDirectorRepository implements DirectorRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final RowMapper<Director> mapper = (rs, rowNum) ->
            new Director(rs.getInt("id"), rs.getString("name"));

    // 1. ПОЛУЧЕНИЕ ВСЕХ РЕЖИССЕРОВ
    private static final String FIND_ALL_QUERY = "SELECT * FROM directors ORDER BY id";
    // 2. ПОИСК РЕЖИССЕРА ПО ID
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE id = :id";
    // 3. СОЗДАНИЕ РЕЖИССЕРА
    private static final String INSERT_QUERY = "INSERT INTO directors (name) VALUES (:name)";
    // 4. ОБНОВЛЕНИЕ РЕЖИССЕРА
    private static final String UPDATE_QUERY = "UPDATE directors SET name = :name WHERE id = :id";
    // 5. УДАЛЕНИЕ РЕЖИССЕРА ПО ID
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM directors WHERE id = :id";
    // 6. ПОЛУЧЕНИЕ РЕЖИССЕРА/РЕЖИССЕРОВ ФИЛЬМА
    private static final String GET_DIRECTORS_BY_FILM_ID_QUERY = "SELECT d.* FROM directors AS d " +
            "JOIN film_director AS fd ON d.id = fd.director_id " +
            "WHERE fd.film_id = :filmId " +
            "ORDER BY d.id";
    // 7. ДОБАВЛЕНИЕ РЕЖИССЕРА В ФИЛЬМ
    private static final String ADD_DIRECTOR_TO_FILM_QUERY =
            "INSERT INTO film_director (film_id, director_id) VALUES (:filmId, :directorId)";
    // 8. УДАЛЕНИЕ ВСЕХ РЕЖИССЕРОВ ФИЛЬМА
    private static final String REMOVE_ALL_DIRECTORS_FROM_FILM_QUERY = "DELETE FROM film_director " +
            "WHERE film_id = :filmId";

    // Создание режиссера (возвращает объект с установленным id)
    @Override
    public Director createDirector(Director director) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", director.getName());

        int id = insert(params);
        director.setId(id);

        return director;
    }

    // Получение списка всех режиссеров
    @Override
    public List<Director> findAllDirectors() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

    // Получение режиссера по ID
    @Override
    public Optional<Director> findDirectorById(int id) {
        try {
            Director result = jdbc.queryForObject(
                    FIND_BY_ID_QUERY,
                    new MapSqlParameterSource("id", id),
                    mapper
            );
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    // Получение списка режиссеров фильма по его ID
    @Override
    public Set<Director> getDirectorsByFilmId(int filmId) {
        List<Director> directors = jdbc.query(
                GET_DIRECTORS_BY_FILM_ID_QUERY,
                new MapSqlParameterSource("filmId", filmId),
                mapper
        );
        return new LinkedHashSet<>(directors);
    }

    // Обновление режиссеров фильма
    @Override
    public void updateDirectorsForFilm(Film film) {
        // 1. Удаляем всех текущих режиссеров фильма, чтобы избежать дубликатов и убрать лишние
        jdbc.update(REMOVE_ALL_DIRECTORS_FROM_FILM_QUERY,
                new MapSqlParameterSource("filmId", film.getId()));

        if (film.getDirectors() == null || film.getDirectors().isEmpty()) {
            return; // Если новых режиссеров нет, просто выходим
        }

        // 2. Готовим параметры для пакетной вставки новых режиссеров
        SqlParameterSource[] batchParams = film.getDirectors().stream()
                .map(director -> new MapSqlParameterSource()
                        .addValue("filmId", film.getId())
                        .addValue("directorId", director.getId()))
                .toArray(SqlParameterSource[]::new);

        // 3. Выполняем пакетную вставку всех режиссеров фильма одним запросом
        if (batchParams.length > 0) {
            jdbc.batchUpdate(ADD_DIRECTOR_TO_FILM_QUERY, batchParams);
        }
    }

    // Обновление режиссера (true — если обновлена хотя бы одна строка)
    @Override
    public boolean updateDirector(Director director) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", director.getName())
                .addValue("id", director.getId());

        return jdbc.update(UPDATE_QUERY, params) > 0;
    }

    // Удаление режиссера по ID (true — если удален)
    @Override
    public boolean deleteDirectorByID(int id) {
        return jdbc.update(DELETE_BY_ID_QUERY,
                new MapSqlParameterSource("id", id)) > 0;
    }

    // Вспомогательный метод для вставки нового объекта
    private int insert(SqlParameterSource params) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(INSERT_QUERY, params, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);

        // Возвращаем id нового объекта
        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }
}
