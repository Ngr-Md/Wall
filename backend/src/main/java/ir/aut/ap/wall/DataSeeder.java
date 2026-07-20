package ir.aut.ap.wall;

import ir.aut.ap.wall.model.Role;
import ir.aut.ap.wall.model.User;
import ir.aut.ap.wall.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DataSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin1234"));
            admin.setPhone("09000000000");
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println(">>> Admin user created: admin / admin1234");
        }
    }
}