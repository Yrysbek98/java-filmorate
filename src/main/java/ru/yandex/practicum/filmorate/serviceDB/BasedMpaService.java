package ru.yandex.practicum.filmorate.serviceDB;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;
@Service
@RequiredArgsConstructor
public class BasedMpaService implements  MpaServiceDB{
    @Override
    public int findMpaIdByName(String mpaName) {
        return 0;
    }

    @Override
    public MPA getMpaById(int id) {
        return null;
    }

    @Override
    public List<MPA> getAllMpa() {
        return List.of();
    }
}
