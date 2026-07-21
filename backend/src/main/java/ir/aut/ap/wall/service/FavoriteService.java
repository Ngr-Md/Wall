package ir.aut.ap.wall.service;

import ir.aut.ap.wall.dto.Dtos.AdSummaryDto;
import ir.aut.ap.wall.exception.ApiException;
import ir.aut.ap.wall.model.Advertisement;
import ir.aut.ap.wall.model.Favorite;
import ir.aut.ap.wall.model.User;
import ir.aut.ap.wall.repository.AdvertisementRepository;
import ir.aut.ap.wall.repository.FavoriteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final AdvertisementRepository advertisementRepository;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           AdvertisementRepository advertisementRepository) {
        this.favoriteRepository = favoriteRepository;
        this.advertisementRepository = advertisementRepository;
    }

    public boolean isFavorite(User user, Long adId) {
        return favoriteRepository.existsByUserIdAndAdvertisementId(user.getId(), adId);
    }

    public void add(User user, Long adId) {
        Advertisement ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> ApiException.notFound("آگهی پیدا نشد"));
        if (!favoriteRepository.existsByUserIdAndAdvertisementId(user.getId(), adId)) {
            favoriteRepository.save(new Favorite(user, ad));
        }
    }

    public void remove(User user, Long adId) {
        favoriteRepository.findByUserIdAndAdvertisementId(user.getId(), adId)
                .ifPresent(favoriteRepository::delete);
    }

    public List<AdSummaryDto> list(User user) {
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(f -> AdSummaryDto.of(f.getAdvertisement()))
                .toList();
    }
}