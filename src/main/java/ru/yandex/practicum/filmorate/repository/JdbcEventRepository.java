package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;


import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcEventRepository implements EventRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public List<Event> getUsersEvents(Integer userId) {
        String query = """
                SELECT e.*
                FROM EVENTS e
                WHERE e.USER_ID = :userId
                ORDER BY e.TIMESTAMP ASC
                """;

        Map<String, Object> params = Map.of("userId", userId);

        return jdbc.query(query, params, (rs, rowNum) -> new Event(
                rs.getInt("event_id"),
                rs.getLong("timestamp"),
                rs.getInt("user_id"),
                EventType.valueOf(rs.getString("event_type")),
                Operation.valueOf(rs.getString("operation")),
                rs.getInt("entity_id")
        ));
    }

    @Override
    public void addEvent(Event event) {
        String sql = """
                INSERT INTO EVENTS ( timestamp, user_id, event_type, operation, entity_id)
                VALUES ( :timestamp, :userId, :eventType, :operation, :entityId)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("timestamp", event.getTimestamp())
                .addValue("userId", event.getUserId())
                .addValue("eventType", event.getEventType().name())
                .addValue("operation", event.getOperation().name())
                .addValue("entityId", event.getEntityId());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(sql, params, keyHolder, new String[]{"event_id"});
    }
}
