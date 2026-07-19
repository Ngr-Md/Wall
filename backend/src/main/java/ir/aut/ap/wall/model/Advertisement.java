package ir.aut.ap.wall.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "advertisements")
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 4000)
    private String description;

    @Column(nullable = false)
    private Long price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private AdStatus status = AdStatus.PENDING;

    @Column(length = 500)
    private String rejectionReason;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id")
    private User seller;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Lob
    @Column(name = "images")
    private String images = "";

    public Advertisement() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getPrice() { return price; }
    public void setPrice(Long price) { this.price = price; }
    public AdStatus getStatus() { return status; }
    public void setStatus(AdStatus status) { this.status = status; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }
    public User getSeller() { return seller; }
    public void setSeller(User seller) { this.seller = seller; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
}