package ir.aut.ap.wall.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "conversations", uniqueConstraints = @UniqueConstraint(columnNames = {"ad_id", "buyer_id"}))
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "ad_id")
    private Advertisement advertisement;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id")
    private User seller;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Conversation() {
    }

    public Conversation(Advertisement advertisement, User buyer, User seller) {
        this.advertisement = advertisement;
        this.buyer = buyer;
        this.seller = seller;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Advertisement getAdvertisement() { return advertisement; }
    public void setAdvertisement(Advertisement advertisement) { this.advertisement = advertisement; }
    public User getBuyer() { return buyer; }
    public void setBuyer(User buyer) { this.buyer = buyer; }
    public User getSeller() { return seller; }
    public void setSeller(User seller) { this.seller = seller; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}