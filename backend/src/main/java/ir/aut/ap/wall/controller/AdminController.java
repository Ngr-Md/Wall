package ir.aut.ap.wall.controller;

import ir.aut.ap.wall.dto.Dtos.*;
import ir.aut.ap.wall.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/ads/pending")
    public List<AdSummaryDto> pendingAds() {
        return adminService.pendingAds();
    }

    @PostMapping("/ads/{id}/review")
    public AdDetailsDto review(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        return adminService.review(id, request);
    }

    @GetMapping("/users")
    public List<UserDto> users() {
        return adminService.users();
    }

    @PostMapping("/users/{id}/block")
    public UserDto block(@PathVariable Long id) {
        return adminService.setBlocked(id, true);
    }

    @PostMapping("/users/{id}/unblock")
    public UserDto unblock(@PathVariable Long id) {
        return adminService.setBlocked(id, false);
    }
}