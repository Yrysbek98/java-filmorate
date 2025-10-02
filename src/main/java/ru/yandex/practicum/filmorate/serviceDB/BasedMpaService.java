package ru.yandex.practicum.filmorate.serviceDB;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;

import ru.yandex.practicum.filmorate.exception.MpaValidationException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.repository.MpaRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasedMpaService implements MpaServiceDB {
    final MpaRepository mpaRepository;

    @Override
    public Optional<Integer> findMpaIdByName(String mpaName) {
        final int id = mpaRepository.findMpaIdByName(mpaName);
        return Optional.of(id);
    }

    @Override
    public MPA getMpaById(int id) {
        if (id < 1) {
            throw new MpaValidationException("Некорректный id рейтинга");
        }
        if (id > 5) {
            throw new MpaNotFoundException("Нет такого рейтинга");
        }
        return mpaRepository.getMpaById(id);
    }

    @Override
    public List<MPA> getAllMpa() {
        return mpaRepository.getAllMpa();
    }
}
