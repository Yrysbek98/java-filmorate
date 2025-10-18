package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DirectorRepository {
    // Создание режиссера
    Director createDirector(Director director);

    // Поиск всех режиссеров
    List<Director> findAllDirectors();

    // Поиск режиссера по ID
    Optional<Director> findDirectorById(int id);

    // Обновление режиссера
    boolean updateDirector(Director director);

    // Удаление режиссера по ID
    boolean deleteDirectorByID(int id);

    // Получение списка режиссеров фильма по его ID
    Set<Director> getDirectorsByFilmId(int filmId);

    // Обновление режиссеров фильма
    void updateDirectorsForFilm(Film film);
}
