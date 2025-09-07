package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film changeFilm(Film film) {
        return filmStorage.changeFilm(film);
    }

    public Film addLike(int id, int userId) {
        if (!userStorage.checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        }
        return filmStorage.addLike(id, userId);
    }

    public Film deleteLike(int id, int userId) {
        if (!userStorage.checkUser(userId)) {
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        }
        return filmStorage.deleteLike(id, userId);
    }

    public Collection<Film> getTopFilms(int count) {
        return filmStorage.getTopFilms(count);
    }

}
