package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;
import java.util.Optional;

public interface MpaRepository {
    int findMpaIdByName(String mpaName);

   Optional<MPA>  getMpaById(int id);

    List<MPA> getAllMpa();
}
