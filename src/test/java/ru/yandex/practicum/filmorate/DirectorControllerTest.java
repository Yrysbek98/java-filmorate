package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.controller.DirectorController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.director.DirectorRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DirectorControllerTest {
    @Autowired
    private DirectorController directorController;

    @Autowired
    private DirectorRepository directorRepository;

    @BeforeEach
    void setup() {
        // Очистка таблицы перед каждым тестом
        directorRepository.findAllDirectors().forEach(d ->
                directorRepository.deleteDirectorByID(d.getId()));
    }

    @Test
    @DisplayName("addDirector должен создавать режиссера")
    void shouldCreateDirector() {
        Director newDirector = new Director(0, "Christopher Nolan");

        Director created = directorController.addDirector(newDirector);

        assertEquals("Christopher Nolan", created.getName());

        List<Director> all = directorController.getAllDirectors();
        assertEquals(1, all.size());
        assertEquals("Christopher Nolan", all.getFirst().getName());
    }

    @Test
    @DisplayName("updateDirector должен обновлять имя режиссера")
    void shouldUpdateDirector() {
        Director director = directorController.addDirector(new Director(0, "Old Name"));

        Director updated = new Director(director.getId(), "New Name");
        Director result = directorController.updateDirector(updated);

        assertEquals("New Name", result.getName());

        Director fromDb = directorController.getDirectorById(director.getId());
        assertEquals("New Name", fromDb.getName());
    }

    @Test
    @DisplayName("deleteDirectorByID должен удалять режиссера")
    void shouldDeleteDirector() {
        Director director = directorController.addDirector(new Director(0, "ToDelete"));

        directorController.deleteDirectorByID(director.getId());

        List<Director> all = directorController.getAllDirectors();
        assertTrue(all.isEmpty());
    }

    @Test
    @DisplayName("getDirectorById должен вернуть режиссера по ID")
    void shouldReturnDirectorById() {
        Director saved = directorController.addDirector(new Director(0, "Denis Villeneuve"));

        Director result = directorController.getDirectorById(saved.getId());

        assertNotNull(result);
        assertEquals("Denis Villeneuve", result.getName());
    }
}
