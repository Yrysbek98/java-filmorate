package ru.yandex.practicum.filmorate.serviceDB.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.repository.event.EventRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BasedEventService implements EventServiceDB {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<Event> getUsersEvents(Integer userId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id=" + userId + " не найден"));

        return eventRepository.getUsersEvents(userId);
    }
}
