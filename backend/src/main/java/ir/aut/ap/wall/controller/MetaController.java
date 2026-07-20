package ir.aut.ap.wall.controller;

import ir.aut.ap.wall.dto.Dtos.NamedDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meta")
public class MetaController {

    private static final List<NamedDto> CATEGORIES = List.of(
            new NamedDto(1L, "الکترونیک"),
            new NamedDto(2L, "وسایل خانه"),
            new NamedDto(3L, "وسایل نقلیه"),
            new NamedDto(4L, "پوشاک"),
            new NamedDto(5L, "کتاب و لوازم تحریر"),
            new NamedDto(6L, "ورزشی"),
            new NamedDto(7L, "سایر")
    );

    private static final List<NamedDto> CITIES = List.of(
            new NamedDto(1L, "تهران"),
            new NamedDto(2L, "مشهد"),
            new NamedDto(3L, "اصفهان"),
            new NamedDto(4L, "شیراز"),
            new NamedDto(5L, "تبریز"),
            new NamedDto(6L, "کرج"),
            new NamedDto(7L, "اهواز"),
            new NamedDto(8L, "قم"),
            new NamedDto(9L, "رشت")
    );

    @GetMapping("/categories")
    public ResponseEntity<List<NamedDto>> getCategories() {
        return ResponseEntity.ok(CATEGORIES);
    }

    @GetMapping("/cities")
    public ResponseEntity<List<NamedDto>> getCities() {
        return ResponseEntity.ok(CITIES);
    }
}