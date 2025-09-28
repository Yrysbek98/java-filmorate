package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

public interface MpaRepository {
    int findMpaIdByName(String mpaName);

    MPA getMpaById(int id);

    List<MPA> getAllMpa();
}
