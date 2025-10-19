package ru.yandex.practicum.filmorate.serviceDB.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.mpa.MpaNotFoundException;

import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepository;

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
    public Optional<Mpa> getMpaById(int id) {
        Optional<Mpa> mpa = mpaRepository.getMpaById(id);
        if (mpa.isEmpty()) {
            throw new MpaNotFoundException("Рейтинг с таким id= " + id + " не найден");
        }
        return mpaRepository.getMpaById(id);
    }

    @Override
    public List<Mpa> getAllMpa() {
        return mpaRepository.getAllMpa();
    }
}
