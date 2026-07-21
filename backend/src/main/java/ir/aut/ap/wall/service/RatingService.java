package ir.aut.ap.wall.service;

import ir.aut.ap.wall.dto.Dtos.*;
import ir.aut.ap.wall.exception.ApiException;
import ir.aut.ap.wall.model.Advertisement;
import ir.aut.ap.wall.model.Rating;
import ir.aut.ap.wall.model.User;
import ir.aut.ap.wall.repository.AdvertisementRepository;
import ir.aut.ap.wall.repository.RatingRepository;
import ir.aut.ap.wall.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final AdvertisementRepository advertisementRepository;
    private final UserRepository userRepository;

    public RatingService(RatingRepository ratingRepository,
                         AdvertisementRepository advertisementRepository,
                         UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.advertisementRepository = advertisementRepository;
        this.userRepository = userRepository;
    }

    public RatingDto rate(User user, RatingRequest request) {
        Advertisement ad = advertisementRepository.findById(request.adId())
                .orElseThrow(() -> ApiException.notFound("آگهی پیدا نشد"));
        if (ad.getSeller().getId().equals(user.getId())) {
            throw ApiException.badRequest("نمی‌توانید به آگهی خودتان امتیاز بدهید");
        }
        if (ratingRepository.existsByRaterIdAndAdvertisementId(user.getId(), ad.getId())) {
            throw ApiException.conflict("شما قبلاً به این آگهی امتیاز داده‌اید");
        }
        String comment = request.comment() == null ? null : request.comment().trim();
        Rating rating = new Rating(user, ad.getSeller(), ad, request.score(), comment);
        return RatingDto.of(ratingRepository.save(rating));
    }

    public SellerRatingsDto sellerRatings(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> ApiException.notFound("فروشنده پیدا نشد"));
        List<Rating> ratings = ratingRepository.findBySellerIdOrderByCreatedAtDesc(sellerId);
        Double average = null;
        if (!ratings.isEmpty()) {
            long sum = 0;
            for (Rating r : ratings) {
                sum += r.getScore();
            }
            average = (double) sum / ratings.size();
        }
        List<RatingDto> dtos = ratings.stream().map(RatingDto::of).toList();
        return new SellerRatingsDto(seller.getId(), seller.getUsername(), average, dtos);
    }
}