package ir.aut.ap.wall.service;

import ir.aut.ap.wall.dto.Dtos.*;
import ir.aut.ap.wall.exception.ApiException;
import ir.aut.ap.wall.model.*;
import ir.aut.ap.wall.repository.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ir.aut.ap.wall.model.Rating;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AdService {

    private final AdvertisementRepository adRepository;
    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;
    private final RatingRepository ratingRepository;

    public AdService(AdvertisementRepository adRepository,
                     CategoryRepository categoryRepository,
                     CityRepository cityRepository,
                     RatingRepository ratingRepository) {
        this.adRepository = adRepository;
        this.categoryRepository = categoryRepository;
        this.cityRepository = cityRepository;
        this.ratingRepository = ratingRepository;
    }

    @Transactional
    public AdDetailsDto create(User seller, AdRequest request) {
        Advertisement ad = new Advertisement();
        applyRequest(ad, request);
        ad.setSeller(seller);
        ad.setStatus(AdStatus.PENDING);
        adRepository.save(ad);
        return toDetails(ad);
    }

    @Transactional
    public AdDetailsDto update(User user, Long adId, AdRequest request) {
        Advertisement ad = getOrThrow(adId);
        if (!ad.getSeller().getId().equals(user.getId())) {
            throw ApiException.forbidden("فقط صاحب آگهی می‌تواند آن را ویرایش کند");
        }
        if (ad.getStatus() == AdStatus.SOLD) {
            throw ApiException.badRequest("آگهی فروخته‌شده قابل ویرایش نیست");
        }
        applyRequest(ad, request);
        ad.setStatus(AdStatus.PENDING);
        ad.setRejectionReason(null);
        adRepository.save(ad);
        return toDetails(ad);
    }

    @Transactional
    public void delete(User user, Long adId) {
        Advertisement ad = getOrThrow(adId);
        boolean isOwner = ad.getSeller().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw ApiException.forbidden("اجازه حذف این آگهی را ندارید");
        }
        adRepository.delete(ad);
    }

    @Transactional
    public AdDetailsDto markSold(User user, Long adId) {
        Advertisement ad = getOrThrow(adId);
        if (!ad.getSeller().getId().equals(user.getId())) {
            throw ApiException.forbidden("فقط صاحب آگهی می‌تواند وضعیت فروش را ثبت کند");
        }
        if (ad.getStatus() != AdStatus.APPROVED) {
            throw ApiException.badRequest("فقط آگهی تاییدشده را می‌توان فروخته‌شده علامت زد");
        }
        ad.setStatus(AdStatus.SOLD);
        adRepository.save(ad);
        return toDetails(ad);
    }

    @Transactional(readOnly = true)
    public List<AdSummaryDto> search(String q, Long categoryId, Long cityId,
                                     Long minPrice, Long maxPrice, String sort) {
        List<Advertisement> all = adRepository.findByStatusOrderByCreatedAtDesc(AdStatus.APPROVED);

        Comparator<Advertisement> comparator = switch (sort == null ? "newest" : sort) {
            case "oldest" -> Comparator.comparing(Advertisement::getCreatedAt);
            case "price_asc" -> Comparator.comparingLong(Advertisement::getPrice);
            case "price_desc" -> Comparator.comparingLong(Advertisement::getPrice).reversed();
            default -> Comparator.comparing(Advertisement::getCreatedAt).reversed();
        };

        return all.stream()
                .filter(a -> q == null || q.isBlank() ||
                        a.getTitle().toLowerCase().contains(q.toLowerCase()) ||
                        a.getDescription().toLowerCase().contains(q.toLowerCase()))
                .filter(a -> categoryId == null || a.getCategory().getId().equals(categoryId))
                .filter(a -> cityId == null || a.getCity().getId().equals(cityId))
                .filter(a -> minPrice == null || a.getPrice() >= minPrice)
                .filter(a -> maxPrice == null || a.getPrice() <= maxPrice)
                .sorted(comparator)
                .map(AdSummaryDto::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdDetailsDto details(Long adId) {
        return toDetails(getOrThrow(adId));
    }

    @Transactional(readOnly = true)
    public List<AdSummaryDto> myAds(User user) {
        return adRepository.findBySellerIdOrderByCreatedAtDesc(user.getId())
                .stream().map(AdSummaryDto::of).toList();
    }

    public Advertisement getOrThrow(Long adId) {
        return adRepository.findById(adId)
                .orElseThrow(() -> ApiException.notFound("آگهی یافت نشد"));
    }


    private AdDetailsDto toDetails(Advertisement ad) {
        List<Rating> ratings = ratingRepository.findBySellerIdOrderByCreatedAtDesc(ad.getSeller().getId());
        Double avg = null;
        if (!ratings.isEmpty()) {
            double sum = 0;
            for (Rating r : ratings) {
                sum += r.getScore();
            }
            avg = sum / ratings.size();
        }
        return AdDetailsDto.of(ad, avg);
    }

    private void applyRequest(Advertisement ad, AdRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> ApiException.badRequest("دسته‌بندی نامعتبر است"));
        City city = cityRepository.findById(request.cityId())
                .orElseThrow(() -> ApiException.badRequest("شهر نامعتبر است"));
        ad.setTitle(request.title().trim());
        ad.setDescription(request.description().trim());
        ad.setPrice(request.price());
        ad.setCategory(category);
        ad.setCity(city);
        String images = (request.imagesBase64() == null || request.imagesBase64().isEmpty())
                ? "" : String.join("|||", request.imagesBase64());
        ad.setImages(images);
    }
}