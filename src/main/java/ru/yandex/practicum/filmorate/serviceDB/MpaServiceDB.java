package ru.yandex.practicum.filmorate.serviceDB;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaServiceDB {
    Optional<Integer> findMpaIdByName(String mpaName);

    Optional<Mpa> getMpaById(int id);

    List<Mpa> getAllMpa();

}
