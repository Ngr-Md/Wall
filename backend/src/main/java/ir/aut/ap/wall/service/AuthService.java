package ir.aut.ap.wall.service;

import ir.aut.ap.wall.dto.Dtos;
import ir.aut.ap.wall.dto.Dtos.*;
import ir.aut.ap.wall.exception.ApiException;
import ir.aut.ap.wall.model.User;
import ir.aut.ap.wall.repository.UserRepository;
import ir.aut.ap.wall.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ir.aut.ap.wall.model.Role;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.findByUsername(req.username()).isPresent()) {
            throw ApiException.conflict("نام کاربریت کپیه! بیشتر خلاق باش");
        }

        User user = new User();
        user.setUsername(req.username());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setFullName(req.fullName());
        user.setEmail(req.email());
        user.setPhone(req.phone());
        user.setRole(ir.aut.ap.wall.model.Role.USER);
        userRepository.save(user);
        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, Dtos.UserDto.of(user));
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.username())
                .orElseThrow(() -> ApiException.unauthorized("یچیو اشتباه زدی(نام کاربری یا رمز عبورت رو چک کن)"));
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw ApiException.unauthorized("یچیو اشتباه زدی(نام کاربری یا رمز عبورت رو چک کن)");
        }
        if (user.isBlocked()) {
            throw ApiException.forbidden("حساب کاربری شما مسدود شده است");
        }
        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, Dtos.UserDto.of(user));
    }
}