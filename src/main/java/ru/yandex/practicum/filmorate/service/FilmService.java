package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Set;

@Service
public class FilmService {
    @Autowired
    private FilmStorage filmStorage;
    @Autowired
    private UserStorage userStorage;

    public void addLike(int idUser, int idFilm){
        Set<Integer> likes = userStorage.getSetOfLikes(idUser);
        if (!likes.contains(idFilm)){
            filmStorage.addLike(idFilm);
        }
    }

    public void deleteLike(int idUser, int idFilm){
        Set<Integer> likes = userStorage.getSetOfLikes(idUser);
        if (!likes.contains(idFilm)){
            filmStorage.deleteLike(idFilm);
        }
    }



}
