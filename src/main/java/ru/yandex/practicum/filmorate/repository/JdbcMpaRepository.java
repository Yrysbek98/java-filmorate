package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class JdbcMpaRepository implements MpaRepository {

    private final NamedParameterJdbcOperations jdbc;

    @Override
    public int findMpaIdByName(String mpaName) {
        String query = "SELECT mpa_id FROM MPA WHERE name = :name";
        Map<String, Object> params = Map.of("name", mpaName);
        return jdbc.queryForObject(query, params, Integer.class);
    }

    @Override
    public Optional<MPA> getMpaById(int id) {
        String query = """
            SELECT mpa_id, name
            FROM MPA
            WHERE mpa_id = :id
            """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        List<MPA> result = jdbc.query(query, params, (rs, rowNum) ->
                new MPA(
                        rs.getInt("mpa_id"),
                        rs.getString("name")
                )
        );

        return result.stream().findFirst();

    }

    @Override
    public List<MPA> getAllMpa() {
        String query = """
                SELECT m.mpa_id, m.name
                FROM MPA AS m
                """;
        Map<Integer, MPA> mpaMap = new LinkedHashMap<>();
        jdbc.query(query, rs -> {
            int mpaId = rs.getInt("mpa_id");
            MPA mpa = new MPA(
                    mpaId,
                    rs.getString("name")
            );
            mpaMap.put(mpaId, mpa);
        });
        return new ArrayList<>(mpaMap.values());
    }
}
