package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class JdbcUserRepository implements UserRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Optional<User> getUserById(int id) {
        String query = """
                SELECT user_id, email, login, name, birthday
                FROM USERS
                WHERE user_id = :id
                """;

        Map<String, Object> params = Map.of("id", id);

        List<User> users = jdbc.query(query, params, (rs, rowNum) ->
                new User(
                        rs.getInt("user_id"),
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getString("name"),
                        rs.getDate("birthday").toLocalDate()
                )
        );

        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public List<User> findAll() {
        String query = """
                SELECT u.user_id, u.email, u.login, u.name, u.birthday
                FROM USERS AS u
                """;

        Map<Integer, User> userMap = new LinkedHashMap<>();

        jdbc.query(query, rs -> {
            int userId = rs.getInt("user_id");

            User user = new User(
                    userId,  // Добавили ID!
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birthday").toLocalDate()
            );

            userMap.put(userId, user);
        });

        return new ArrayList<>(userMap.values());
    }

    @Override
    public User createUser(User user) {
        String createQuery = """
                INSERT INTO USERS(email, login, name, birthday)
                VALUES(:email, :login, :name, :birthday)
                """;
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", user.getEmail());
        params.addValue("login", user.getLogin());
        params.addValue("name", user.getName());
        params.addValue("birthday", user.getBirthday());
        jdbc.update(createQuery, params, keyHolder, new String[]{"user_id"});
        int userId = keyHolder.getKeyAs(Integer.class);
        user.setId(userId);
        return user;
    }

    @Override
    public Optional<User> changeUser(User user) {

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", user.getEmail());
        params.addValue("login", user.getLogin());
        params.addValue("name", user.getName());
        params.addValue("birthday", user.getBirthday());
        params.addValue("id", user.getId());

        String updateUserQuery = """
                UPDATE USERS
                SET EMAIL = :email, LOGIN = :login, NAME = :name, BIRTHDAY = :birthday
                WHERE user_id = :id
                """;
        jdbc.update(updateUserQuery, params);

        return getUserById(user.getId());


    }

    @Override
    public void addFriend(int idOfUser, int idOfFriend) {
        String addFriendQuery = """
                INSERT INTO FRIENDS (user_id, friend_id)
                VALUES (:userId, :friendId)
                """;
        Map<String, Object> params = Map.of(
                "userId", idOfUser,
                "friendId", idOfFriend
        );
        jdbc.update(addFriendQuery, params);
    }

    @Override
    public void deleteFriend(int idOfUser, int idOfFriend) {
        String deleteFriendQuery = """
                 DELETE FROM FRIENDS
                 WHERE user_id = :userId AND friend_id = :friendId
                """;
        Map<String, Object> params = Map.of(
                "userId", idOfUser,
                "friendId", idOfFriend
        );
        jdbc.update(deleteFriendQuery, params);
    }

    @Override
    public List<User> getSameFriends(int idOfUser, int idOfFriend) {
        String sameFriendsQuery = """
                SELECT u.user_id, u.email, u.login, u.name, u.birthday
                FROM FRIENDS f1
                JOIN FRIENDS f2 ON f1.friend_id = f2.friend_id
                JOIN USERS u ON u.user_id = f1.friend_id
                WHERE f1.user_id = :idOfUser
                  AND f2.user_id = :idOfFriend
                """;

        Map<String, Object> params = Map.of(
                "idOfUser", idOfUser,
                "idOfFriend", idOfFriend
        );

        return jdbc.query(sameFriendsQuery, params, (rs, rowNum) -> new User(
                rs.getInt("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()
        ));
    }

    @Override
    public List<User> getFriends(int userId) {
        String sql = """
                    SELECT u.user_id, u.email, u.login, u.name, u.birthday
                    FROM FRIENDS f
                    JOIN USERS u ON u.user_id = f.friend_id
                    WHERE f.user_id = :userId
                """;

        return jdbc.query(sql, Map.of("userId", userId), (rs, rowNum) -> new User(
                rs.getInt("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()
        ));
    }


}
