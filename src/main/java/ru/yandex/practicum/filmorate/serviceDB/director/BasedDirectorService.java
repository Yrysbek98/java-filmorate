package ru.yandex.practicum.filmorate.serviceDB.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.director.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.director.DirectorValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.director.DirectorRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasedDirectorService implements DirectorServiceDB {
    private final DirectorRepository directorRepository;

    // Добавление режиссера
    @Override
    public Director createDirector(Director director) {
        log.info("Запрос на создание режиссера '{}'", director.getName());

        // Валидация режиссера
        validateDirector(director);
        Director createdDirector = directorRepository.createDirector(director);

        log.info("Режиссер '{}' с id = {} успешно создан.",
                createdDirector.getName(),
                createdDirector.getId()
        );

        return createdDirector;
    }

    // Получение списка всех режиссеров
    @Override
    public List<Director> findAllDirectors() {
        log.info("Запрос на получение списка всех режиссеров.");

        return directorRepository.findAllDirectors();
    }

    // Получение режиссера по ID
    @Override
    public Director findDirectorById(int id) {
        log.info("Запрос на получение режиссера с id = {}.", id);

        return directorRepository.findDirectorById(id)
                .orElseThrow(() -> new DirectorNotFoundException("Режиссер с id = " + id + " не найден."));
    }

    // Обновление режиссера
    @Override
    public Director updateDirector(Director director) {
        log.info("Запрос на обновление режиссера с id = {}", director.getId());

        // Проверка существования режиссера и его валидация
        validateExistence(director.getId());
        validateDirector(director);

        directorRepository.updateDirector(director);

        log.info("Режиссер с id = {} успешно обновлен.", director.getId());

        return director;
    }

    // Удаление режиссера по ID
    @Override
    public void deleteDirectorByID(int id) {
        log.info("Запрос на удаление режиссера с id = {}", id);

        // Проверка существования пользователя
        validateExistence(id);
        directorRepository.deleteDirectorByID(id);

        log.info("Режиссер с id = {} успешно удален.", id);
    }

    // Метод проверки существования режиссера
    private void validateExistence(int id) {
        if (directorRepository.findDirectorById(id).isEmpty()) {
            throw new DirectorNotFoundException("Режиссер с id = " + id + " не найден.");
        }
    }

    // Метод валидации режиссера
    private void validateDirector(Director director) {
        if (director.getName() == null || director.getName().isBlank()) {
            throw new DirectorValidationException("Имя режиссера не может быть пустым.");
        }
    }
}
