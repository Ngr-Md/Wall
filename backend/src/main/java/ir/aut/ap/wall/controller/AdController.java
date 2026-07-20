package ir.aut.ap.wall.controller;

import ir.aut.ap.wall.dto.Dtos.*;
import ir.aut.ap.wall.model.User;
import ir.aut.ap.wall.service.AdService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ads")
public class AdController {

    private final AdService adService;

    public AdController(AdService adService) {
        this.adService = adService;
    }

    @GetMapping
    public List<AdSummaryDto> search(@RequestParam(required = false) String q,
                                     @RequestParam(required = false) Long categoryId,
                                     @RequestParam(required = false) Long cityId,
                                     @RequestParam(required = false) Long minPrice,
                                     @RequestParam(required = false) Long maxPrice,
                                     @RequestParam(required = false, defaultValue = "newest") String sort) {
        return adService.search(q, categoryId, cityId, minPrice, maxPrice, sort);
    }

    @GetMapping("/mine")
    public List<AdSummaryDto> mine(@AuthenticationPrincipal User user) {
        return adService.myAds(user);
    }

    @GetMapping("/{id}")
    public AdDetailsDto details(@PathVariable Long id) {
        return adService.details(id);
    }

    @PostMapping
    public ResponseEntity<AdDetailsDto> create(@AuthenticationPrincipal User user,
                                               @Valid @RequestBody AdRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adService.create(user, request));
    }

    @PutMapping("/{id}")
    public AdDetailsDto update(@AuthenticationPrincipal User user,
                               @PathVariable Long id,
                               @Valid @RequestBody AdRequest request) {
        return adService.update(user, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        adService.delete(user, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sold")
    public AdDetailsDto markSold(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return adService.markSold(user, id);
    }
}