package ir.aut.ap.wall.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ratings", uniqueConstraints = @UniqueConstraint(columnNames = {"rater_id", "ad_id"}))
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "rater_id")
    private User rater;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id")
    private User seller;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "ad_id")
    private Advertisement advertisement;

    /** امتیاز از ۱ تا ۵ */
    @Column(nullable = false)
    private int score;

    @Column(length = 1000)
    private String comment;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Rating() {
    }

    public Rating(User rater, User seller, Advertisement advertisement, int score, String comment) {
        this.rater = rater;
        this.seller = seller;
        this.advertisement = advertisement;
        this.score = score;
        this.comment = comment;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getRater() { return rater; }
    public void setRater(User rater) { this.rater = rater; }
    public User getSeller() { return seller; }
    public void setSeller(User seller) { this.seller = seller; }
    public Advertisement getAdvertisement() { return advertisement; }
    public void setAdvertisement(Advertisement advertisement) { this.advertisement = advertisement; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}