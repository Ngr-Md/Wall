package ir.aut.ap.wall.controller;

import ir.aut.ap.wall.dto.Dtos.*;
import ir.aut.ap.wall.model.User;
import ir.aut.ap.wall.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/api/ratings")
    public ResponseEntity<RatingDto> rate(@AuthenticationPrincipal User user,
                                          @Valid @RequestBody RatingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.rate(user, request));
    }

    @GetMapping("/api/sellers/{sellerId}/ratings")
    public SellerRatingsDto sellerRatings(@PathVariable Long sellerId) {
        return ratingService.sellerRatings(sellerId);
    }
}