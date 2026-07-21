package ir.aut.ap.wall.controller;

import ir.aut.ap.wall.dto.Dtos.AdSummaryDto;
import ir.aut.ap.wall.model.User;
import ir.aut.ap.wall.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public List<AdSummaryDto> list(@AuthenticationPrincipal User user) {
        return favoriteService.list(user);
    }

    @GetMapping("/{adId}")
    public Map<String, Boolean> status(@AuthenticationPrincipal User user, @PathVariable Long adId) {
        return Map.of("favorite", favoriteService.isFavorite(user, adId));
    }

    @PostMapping("/{adId}")
    public ResponseEntity<Void> add(@AuthenticationPrincipal User user, @PathVariable Long adId) {
        favoriteService.add(user, adId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{adId}")
    public ResponseEntity<Void> remove(@AuthenticationPrincipal User user, @PathVariable Long adId) {
        favoriteService.remove(user, adId);
        return ResponseEntity.noContent().build();
    }
}