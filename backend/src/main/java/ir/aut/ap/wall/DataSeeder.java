package ir.aut.ap.wall;

import ir.aut.ap.wall.model.Category;
import ir.aut.ap.wall.model.City;
import ir.aut.ap.wall.model.Role;
import ir.aut.ap.wall.model.User;
import ir.aut.ap.wall.repository.CategoryRepository;
import ir.aut.ap.wall.repository.CityRepository;
import ir.aut.ap.wall.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DataSeeder(UserRepository userRepository,
                      CategoryRepository categoryRepository,
                      CityRepository cityRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.cityRepository = cityRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin1234"));
            admin.setFullName("مدیر سیستم");
            admin.setEmail("admin@wall.ir");
            admin.setPhone("09000000000");
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println(">>> Admin user created: admin / admin1234");
        }

        if (categoryRepository.count() == 0) {
            List<String> categoryNames = List.of(
                    "الکترونیک", "وسایل خانه", "وسایل نقلیه",
                    "پوشاک", "کتاب و لوازم تحریر", "ورزشی", "سایر"
            );
            for (String name : categoryNames) {
                Category category = new Category();
                category.setName(name);
                categoryRepository.save(category);
            }
            System.out.println(">>> Categories seeded");
        }

        if (cityRepository.count() == 0) {
            List<String> cityNames = List.of(
                    "تهران", "مشهد", "اصفهان", "شیراز", "تبریز",
                    "کرج", "اهواز", "قم", "رشت"
            );
            for (String name : cityNames) {
                City city = new City();
                city.setName(name);
                cityRepository.save(city);
            }
            System.out.println(">>> Cities seeded");
        }
    }
}