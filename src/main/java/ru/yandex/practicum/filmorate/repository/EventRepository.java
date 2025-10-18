package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventRepository {
    List<Event> getUsersEvents(Integer userId);

    void addEvent(Event event);
}
