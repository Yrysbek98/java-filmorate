package ru.yandex.practicum.filmorate.serviceDB;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

public interface MpaServiceDB {
    int findMpaIdByName(String mpaName);

    MPA getMpaById(int id);

    List<MPA> getAllMpa();

}
