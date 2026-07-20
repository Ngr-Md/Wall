package ir.aut.ap.wall.service;

import ir.aut.ap.wall.dto.Dtos.*;
import ir.aut.ap.wall.exception.ApiException;
import ir.aut.ap.wall.model.*;
import ir.aut.ap.wall.repository.AdvertisementRepository;
import ir.aut.ap.wall.repository.RatingRepository;
import ir.aut.ap.wall.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final AdvertisementRepository adRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;

    public AdminService(AdvertisementRepository adRepository,
                        UserRepository userRepository,
                        RatingRepository ratingRepository) {
        this.adRepository = adRepository;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
    }

    @Transactional(readOnly = true)
    public List<AdSummaryDto> pendingAds() {
        return adRepository.findByStatusOrderByCreatedAtAsc(AdStatus.PENDING)
                .stream().map(AdSummaryDto::of).toList();
    }

    @Transactional
    public AdDetailsDto review(Long adId, ReviewRequest request) {
        Advertisement ad = adRepository.findById(adId)
                .orElseThrow(() -> ApiException.notFound("آگهی یافت نشد"));
        if (ad.getStatus() != AdStatus.PENDING) {
            throw ApiException.badRequest("فقط آگهی در انتظار تایید قابل بررسی است");
        }
        if (Boolean.TRUE.equals(request.approve())) {
            ad.setStatus(AdStatus.APPROVED);
            ad.setRejectionReason(null);
        } else {
            if (request.reason() == null || request.reason().isBlank()) {
                throw ApiException.badRequest("برای رد آگهی، ذکر دلیل الزامی است");
            }
            ad.setStatus(AdStatus.REJECTED);
            ad.setRejectionReason(request.reason().trim());
        }
        adRepository.save(ad);
        return AdDetailsDto.of(ad, averageForSeller(ad.getSeller().getId()));
    }

    @Transactional(readOnly = true)
    public List<UserDto> users() {
        return userRepository.findAll().stream().map(UserDto::of).toList();
    }

    @Transactional
    public UserDto setBlocked(Long userId, boolean blocked) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("کاربر یافت نشد"));
        if (user.getRole() == Role.ADMIN) {
            throw ApiException.badRequest("ادمین را نمی‌توان مسدود کرد");
        }
        user.setBlocked(blocked);
        userRepository.save(user);
        return UserDto.of(user);
    }

    private Double averageForSeller(Long sellerId) {
        List<Rating> ratings = ratingRepository.findBySellerIdOrderByCreatedAtDesc(sellerId);
        if (ratings.isEmpty()) {
            return null;
        }
        double sum = 0;
        for (Rating r : ratings) {
            sum += r.getScore();
        }
        return sum / ratings.size();
    }
}