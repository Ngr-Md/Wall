package ir.aut.ap.wall.repository;

import ir.aut.ap.wall.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    boolean existsByRaterIdAndAdvertisementId(Long raterId, Long adId);
}