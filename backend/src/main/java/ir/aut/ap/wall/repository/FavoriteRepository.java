package ir.aut.ap.wall.repository;

import ir.aut.ap.wall.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Favorite> findByUserIdAndAdvertisementId(Long userId, Long adId);
    boolean existsByUserIdAndAdvertisementId(Long userId, Long adId);
}