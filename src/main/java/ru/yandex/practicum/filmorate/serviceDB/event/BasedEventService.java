package ru.yandex.practicum.filmorate.serviceDB.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.repository.event.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BasedEventService implements EventServiceDB {
    final EventRepository eventRepository;

    @Override
    public List<Event> getUsersEvents(Integer userId) {
        return eventRepository.getUsersEvents(userId);
    }
}
