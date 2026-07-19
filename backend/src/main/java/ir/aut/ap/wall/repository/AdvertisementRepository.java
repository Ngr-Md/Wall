package ir.aut.ap.wall.repository;

import ir.aut.ap.wall.model.AdStatus;
import ir.aut.ap.wall.model.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    List<Advertisement> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    List<Advertisement> findByStatusOrderByCreatedAtDesc(AdStatus status);
    List<Advertisement> findByStatusOrderByCreatedAtAsc(AdStatus status);
}