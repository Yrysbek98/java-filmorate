package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
class FilmControllerSearchTest {

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    private int directorId;
    private int film1Id;
    private int film2Id;

    @BeforeEach
    void setup() {
        jdbc.getJdbcTemplate().execute("DELETE FROM film_genres");
        jdbc.getJdbcTemplate().execute("DELETE FROM film_director");
        jdbc.getJdbcTemplate().execute("DELETE FROM films");
        jdbc.getJdbcTemplate().execute("DELETE FROM directors");
        jdbc.getJdbcTemplate().execute("DELETE FROM genres");

        // добавляем жанр
        jdbc.getJdbcTemplate().update("INSERT INTO genres (genre_id, genre_name) VALUES (1, 'Comedy')");

        // добавляем режиссёра
        jdbc.getJdbcTemplate().update("INSERT INTO directors (id, name) VALUES (1, 'Quentin Tarantino')");
        directorId = 1;

        // добавляем фильмы
        jdbc.getJdbcTemplate().update("""
            INSERT INTO films (film_id, name, description, release_date, duration, mpa_id)
            VALUES (1, 'Pulp Fiction', 'Crime movie', '1994-10-14', 154, 1)
        """);
        jdbc.getJdbcTemplate().update("""
            INSERT INTO films (film_id, name, description, release_date, duration, mpa_id)
            VALUES (2, 'Inglourious Basterds', 'War movie', '2009-08-20', 153, 1)
        """);
        film1Id = 1;
        film2Id = 2;

        // добавляем связи
        jdbc.getJdbcTemplate().update("INSERT INTO film_director (film_id, director_id) VALUES (1, 1)");
        jdbc.getJdbcTemplate().update("INSERT INTO film_director (film_id, director_id) VALUES (2, 1)");
        jdbc.getJdbcTemplate().update("INSERT INTO film_genres (film_id, genre_id) VALUES (1, 1)");
        jdbc.getJdbcTemplate().update("INSERT INTO film_genres (film_id, genre_id) VALUES (2, 1)");
    }

    @Test
    @DisplayName("Поиск по названию должен находить фильмы по подстроке")
    void shouldFindByTitle() {
        List<Film> result = filmRepository.searchFilms("fiction", "title");

        assertThat(result).hasSize(1);
        Film film = result.get(0);
        assertThat(film.getName()).isEqualTo("Pulp Fiction");
        assertThat(film.getGenres()).hasSize(1);
        assertThat(film.getDirectors()).hasSize(1);
    }

    @Test
    @DisplayName("Поиск по режиссёру должен находить все фильмы этого режиссёра")
    void shouldFindByDirector() {
        List<Film> result = filmRepository.searchFilms("tarantino", "director");
        assertThat(result).hasSize(2);
        assertThat(result)
                .allSatisfy(film -> assertThat(
                        film.getDirectors()
                                .stream()
                                .anyMatch(d -> d.getName().equals("Quentin Tarantino"))
                ).isTrue());
    }

    @Test
    @DisplayName("Поиск по названию и режиссёру должен объединять результаты")
    void shouldFindByTitleAndDirector() {
        List<Film> result = filmRepository.searchFilms("tarantino", "title,director");

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Film::getName)
                .containsExactlyInAnyOrder("Pulp Fiction", "Inglourious Basterds");
    }

    @Test
    @DisplayName("Поиск по несуществующему запросу должен вернуть пустой список")
    void shouldReturnEmptyIfNoMatch() {
        List<Film> result = filmRepository.searchFilms("nolan", "director");

        assertThat(result).isEmpty();
    }
}

