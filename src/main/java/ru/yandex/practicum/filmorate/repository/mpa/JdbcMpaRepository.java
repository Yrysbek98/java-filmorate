package ru.yandex.practicum.filmorate.repository.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class JdbcMpaRepository implements MpaRepository {

    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Optional<Mpa> getMpaById(int id) {
        String query = """
                SELECT mpa_id, name
                FROM MPA
                WHERE mpa_id = :id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        List<Mpa> result = jdbc.query(query, params, (rs, rowNum) ->
                new Mpa(
                        rs.getInt("mpa_id"),
                        rs.getString("name")
                )
        );

        return result.stream().findFirst();

    }

    @Override
    public List<Mpa> getAllMpa() {
        String query = """
                SELECT m.mpa_id, m.name
                FROM MPA AS m
                """;
        Map<Integer, Mpa> mpaMap = new LinkedHashMap<>();
        jdbc.query(query, rs -> {
            int mpaId = rs.getInt("mpa_id");
            Mpa mpa = new Mpa(
                    mpaId,
                    rs.getString("name")
            );
            mpaMap.put(mpaId, mpa);
        });
        return new ArrayList<>(mpaMap.values());
    }
}
