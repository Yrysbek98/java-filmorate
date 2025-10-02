package ru.yandex.practicum.filmorate.serviceDB;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;
import java.util.Optional;

public interface MpaServiceDB {
    Optional<Integer> findMpaIdByName(String mpaName);

    MPA getMpaById(int id);

    List<MPA> getAllMpa();

}
