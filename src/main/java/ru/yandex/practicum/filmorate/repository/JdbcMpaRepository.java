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
        String query = "SELECT mpa_id FROM MPA WHERE mpa_name = :name";
        Map<String, Object> params = Map.of("name", mpaName);
        return jdbc.queryForObject(query, params, Integer.class);
    }

    @Override
    public Optional<MPA> getMpaById(int id) {
        String query = """
                SELECT mpa_id, mpa_name
                FROM MPA
                WHERE  mpa_id = :id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);


        return Optional.ofNullable(jdbc.queryForObject(query, params, (rs, rowNum) -> {
            int genreId = rs.getInt("mpa_id");
            String genreName = rs.getString("mpa_name");
            return new MPA(genreId, genreName);
        }));
    }

    @Override
    public List<MPA> getAllMpa() {
        String query = "SELECT mpa_id, mpa_name  FROM MPA";
        Map<Integer, MPA> mpaMap = new LinkedHashMap<>();
        jdbc.query(query, rs -> {
            int mpaId = rs.getInt("user_id");
            MPA mpa = new MPA(
                    rs.getInt("id"),
                    rs.getString("name")
            );
            mpaMap.put(mpaId, mpa);
        });
        return new ArrayList<>(mpaMap.values());
    }


}
