package ru.yandex.practicum.filmorate.serviceDB.director;

import ru.yandex.practicum.filmorate.model.Director;
import java.util.List;

public interface DirectorServiceDB {
    // Создание режиссера
    Director createDirector(Director director);

    // Поиск всех режиссеров
    List<Director> findAllDirectors();

    // Поиск режиссера по ID
    Director findDirectorById(int id);

    // Обновление режиссера
    Director updateDirector(Director director);

    // Удаление режиссера по ID
    void deleteDirectorByID(int id);
}
