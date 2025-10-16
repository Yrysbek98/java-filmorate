package ru.yandex.practicum.filmorate.serviceDB;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserRepository userRepository;
    private final FilmRepository filmRepository;

    public List<Film> getRecommendations(int userId) {
        // Получаем все лайки пользователей
        Map<Integer, Set<Integer>> allUsersLikes = userRepository.getAllUsersLikes();

        // Получаем лайки текущего пользователя
        Set<Integer> currentUserLikes = new HashSet<>(userRepository.getLikedFilmsByUser(userId));

        // Находим наиболее похожего пользователя
        Integer mostSimilarUserId = findMostSimilarUser(userId, allUsersLikes, currentUserLikes);

        if (mostSimilarUserId == null) {
            log.info("Не найдено похожих пользователей для userId: {}", userId);
            return new ArrayList<>();
        }

        // Получаем фильмы похожего пользователя, которые текущий пользователь не лайкал
        Set<Integer> similarUserLikes = allUsersLikes.get(mostSimilarUserId);
        Set<Integer> recommendations = new HashSet<>(similarUserLikes);
        recommendations.removeAll(currentUserLikes);

        log.info("Для пользователя {} найдено {} рекомендаций от пользователя {}",
                userId, recommendations.size(), mostSimilarUserId);

        // Возвращаем фильмы с полной информацией
        return filmRepository.getFilmsByIds(new ArrayList<>(recommendations));
    }

    private Integer findMostSimilarUser(int userId, Map<Integer, Set<Integer>> allUsersLikes,
                                        Set<Integer> currentUserLikes) {
        double maxSimilarity = 0.0;
        Integer mostSimilarUserId = null;

        for (Map.Entry<Integer, Set<Integer>> entry : allUsersLikes.entrySet()) {
            int otherUserId = entry.getKey();

            // Пропускаем текущего пользователя
            if (otherUserId == userId) {
                continue;
            }

            Set<Integer> otherUserLikes = entry.getValue();

            // Вычисляем коэффициент схожести (Jaccard similarity)
            double similarity = calculateSimilarity(currentUserLikes, otherUserLikes);

            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                mostSimilarUserId = otherUserId;
            }
        }

        // Исправлено: было mostSimilarity, должно быть maxSimilarity
        return maxSimilarity > 0 ? mostSimilarUserId : null;
    }

    private double calculateSimilarity(Set<Integer> user1Likes, Set<Integer> user2Likes) {
        if (user1Likes.isEmpty() || user2Likes.isEmpty()) {
            return 0.0;
        }

        Set<Integer> intersection = new HashSet<>(user1Likes);
        intersection.retainAll(user2Likes);

        Set<Integer> union = new HashSet<>(user1Likes);
        union.addAll(user2Likes);


        return (double) intersection.size() / union.size();
    }
}