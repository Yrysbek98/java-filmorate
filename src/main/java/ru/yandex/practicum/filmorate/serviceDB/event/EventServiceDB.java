package ru.yandex.practicum.filmorate.serviceDB.event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventServiceDB {

    List<Event> getUsersEvents(Integer userId);
}
