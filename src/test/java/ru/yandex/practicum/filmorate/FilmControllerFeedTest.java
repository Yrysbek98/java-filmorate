package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.event.JdbcEventRepository;

import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmControllerFeedTest {

    @Autowired
    private JdbcEventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FilmRepository filmRepository;


    private User user1;
    private Film film1;
    @BeforeEach
    void setUp() {
        user1 = userRepository.createUser(
                new User(1, "user1@mail.com", "user1", "User One", LocalDate.of(1990, 1, 1))
        );

        film1 = filmRepository.createFilm(
                new Film(101, "Film 1", "Description", LocalDate.of(2020, 1, 1), 120)
        );


        eventRepository.addEvent(new Event(
                System.currentTimeMillis(),
                user1.getId(),
                EventType.FRIEND,
                Operation.ADD,
                2
        ));


        eventRepository.addEvent(new Event(
                System.currentTimeMillis() + 1000,
                user1.getId(),
                EventType.LIKE,
                Operation.ADD,
                film1.getId()
        ));
    }

    @Test
    @DisplayName("Должен возвращать все события пользователя")
    void shouldReturnAllEventsForUser() {
        List<Event> events = eventRepository.getUsersEvents(1);

        assertThat(events).hasSize(2)
                .extracting(Event::getEventType)
                .containsExactly(EventType.FRIEND, EventType.LIKE);
    }

    @Test
    @DisplayName("Должен возвращать пустой список, если у пользователя нет событий")
    void shouldReturnEmptyListForUserWithNoEvents() {
        List<Event> events = eventRepository.getUsersEvents(2);

        assertThat(events).isEmpty();
    }
}
