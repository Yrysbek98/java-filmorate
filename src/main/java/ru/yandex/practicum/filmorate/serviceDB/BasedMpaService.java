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
    public int findMpaIdByName(String mpaName) {
        final int id = mpaRepository.findMpaIdByName(mpaName);
        final MPA mpa = mpaRepository.getMpaById(id).orElseThrow(() -> new MpaNotFoundException("Рейтинг с таким " + mpaName + " не найден"));
        return id;
    }

    @Override
    public Optional<MPA> getMpaById(int id) {
        if (id < 1) {
            throw new MpaValidationException("Некорректный id рейтинга");
        }
        final MPA mpa = mpaRepository.getMpaById(id).orElseThrow(() -> new MpaNotFoundException("Рейтинг с таким " + id + " не найден"));
        return Optional.ofNullable(mpa);
    }

    @Override
    public List<MPA> getAllMpa() {
        return mpaRepository.getAllMpa();
    }
}
