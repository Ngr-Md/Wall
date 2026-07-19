package ir.aut.ap.wall.repository;

import ir.aut.ap.wall.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByAdvertisementIdAndBuyerId(Long adId, Long buyerId);
    List<Conversation> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);
    List<Conversation> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
}