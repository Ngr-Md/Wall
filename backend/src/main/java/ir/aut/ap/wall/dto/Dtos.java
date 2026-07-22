package ir.aut.ap.wall.dto;

import ir.aut.ap.wall.model.*;
import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class Dtos {

    private Dtos() {}

    public record RegisterRequest(
            @NotBlank @Size(min = 4, max = 50)
            @Pattern(regexp = "^[a-zA-Z0-9._]+$",
                    message = "نام کاربری فقط می‌تونه حروف انگلیسی، عدد، نقطه و _ داشته باشه") String username,
            @NotBlank @Size(min = 8, max = 100) String password,
            @NotBlank @Size(max = 100) String fullName,
            @NotBlank @Email String email,
            String phone) {}

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password) {}

    public record AuthResponse(String token, UserDto user) {}

    public record UserDto(Long id, String username, String fullName,
                          String email, String phone, String role) {
        public static UserDto of(User u) {
            return new UserDto(u.getId(), u.getUsername(), u.getFullName(),
                    u.getEmail(), u.getPhone(), u.getRole().name());
        }
    }

    public record NamedDto(Long id, String name) {
        public static NamedDto of(Category c) {
            return new NamedDto(c.getId(), c.getName());
        }
        public static NamedDto of(City c) {
            return new NamedDto(c.getId(), c.getName());
        }
    }

    public record AdRequest(
            @NotBlank @Size(min = 3, max = 120) String title,
            @NotBlank @Size(min = 10, max = 4000) String description,
            @NotNull @Min(0) Long price,
            @NotNull Long categoryId,
            @NotNull Long cityId,
            List<String> imagesBase64) {}

    public record AdSummaryDto(Long id, String title, Long price, String status,
                               String category, String city, String sellerUsername,
                               Instant createdAt, String firstImageBase64) {
        public static AdSummaryDto of(Advertisement a) {
            String firstImage = null;
            String raw = a.getImages();
            if (raw != null && !raw.isEmpty()) {
                firstImage = raw.split("\\|\\|\\|")[0];
            }
            return new AdSummaryDto(a.getId(), a.getTitle(), a.getPrice(),
                    a.getStatus().name(), a.getCategory().getName(),
                    a.getCity().getName(), a.getSeller().getUsername(),
                    a.getCreatedAt(), firstImage);
        }
    }

    public record AdDetailsDto(Long id, String title, String description, Long price,
                               String status, String rejectionReason, NamedDto category,
                               NamedDto city, UserDto seller, Instant createdAt,
                               List<String> imagesBase64, Double sellerAverageRating) {
        public static AdDetailsDto of(Advertisement a, Double sellerAvg) {
            List<String> images = new ArrayList<>();
            String raw = a.getImages();
            if (raw != null && !raw.isEmpty()) {
                images = List.of(raw.split("\\|\\|\\|"));
            }
            return new AdDetailsDto(a.getId(), a.getTitle(), a.getDescription(),
                    a.getPrice(), a.getStatus().name(), a.getRejectionReason(),
                    NamedDto.of(a.getCategory()), NamedDto.of(a.getCity()),
                    UserDto.of(a.getSeller()), a.getCreatedAt(), images, sellerAvg);
        }
    }

    public record ReviewRequest(@NotNull Boolean approve,
                                @Size(max = 500) String reason) {}


    public record StartConversationRequest(@NotNull Long adId) {}

    public record ConversationDto(Long id, Long adId, String adTitle,
                                  UserDto buyer, UserDto seller,
                                  Instant createdAt, long unreadCount) {
        public static ConversationDto of(Conversation c, long unreadCount) {
            return new ConversationDto(c.getId(), c.getAdvertisement().getId(),
                    c.getAdvertisement().getTitle(), UserDto.of(c.getBuyer()),
                    UserDto.of(c.getSeller()), c.getCreatedAt(), unreadCount);
        }
    }

    public record MessageRequest(@NotBlank @Size(max = 2000) String content) {}

    public record MessageDto(Long id, Long conversationId,
                             String senderUsername, String content, Instant sentAt) {
        public static MessageDto of(Message m) {
            return new MessageDto(m.getId(), m.getConversation().getId(),
                    m.getSender().getUsername(), m.getContent(), m.getSentAt());
        }
    }

    public record RatingRequest(
            @NotNull Long adId,
            @NotNull @Min(1) @Max(5) Integer score,
            @Size(max = 1000) String comment) {}

    public record RatingDto(Long id, String raterUsername,
                            int score, String comment, Instant createdAt) {
        public static RatingDto of(Rating r) {
            return new RatingDto(r.getId(), r.getRater().getUsername(),
                    r.getScore(), r.getComment(), r.getCreatedAt());
        }
    }

    public record SellerRatingsDto(Long sellerId, String sellerUsername,
                                   Double average, List<RatingDto> ratings) {}


    public record ApiError(String message) {}
}