package ru.yandex.practicum.filmorate.serviceDB.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;


public interface MpaServiceDB {

    Mpa getMpaById(int id);

    List<Mpa> getAllMpa();

}
