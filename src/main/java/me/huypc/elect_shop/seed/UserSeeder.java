package me.huypc.elect_shop.seed;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.huypc.elect_shop.entity.User;
import me.huypc.elect_shop.entity.User.Role;
import me.huypc.elect_shop.repository.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSeeder implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Seeding users...");

        if (userRepository.count() == 0) {
            // seed
            seedUsers();
            log.info("Users seeded successfully.");
        } else {
            log.info("Users already seeded, skipping...");
        }
    }

    private void seedUsers() {
        List<User> users = List.of(
                User.builder()
                        .username("admin@shop.com")
                        .password(passwordEncoder.encode("admin123"))
                        .firstName("Admin")
                        .lastName("User")
                        .role(Role.ADMIN)
                        .build(),
                User.builder()
                        .username("user@shop.com")
                        .password(passwordEncoder.encode("user123"))
                        .firstName("Regular")
                        .lastName("User")
                        .role(Role.USER)
                        .build());

        userRepository.saveAll(users);
    }

}
