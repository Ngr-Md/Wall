package ir.aut.ap.wall.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "favorites", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "ad_id"}))
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "ad_id")
    private Advertisement advertisement;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Favorite() {
    }

    public Favorite(User user, Advertisement advertisement) {
        this.user = user;
        this.advertisement = advertisement;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Advertisement getAdvertisement() { return advertisement; }
    public void setAdvertisement(Advertisement advertisement) { this.advertisement = advertisement; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}