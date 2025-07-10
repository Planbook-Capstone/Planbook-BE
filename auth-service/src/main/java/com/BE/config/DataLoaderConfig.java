package com.BE.config;

import com.BE.enums.RoleEnum;
import com.BE.model.entity.User;
import com.BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataLoaderConfig implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataLoaderConfig.class);


    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {
        log.info("Checking and initializing default users...");

        createUserIfNotExist("admin", "admin@gmail.com", RoleEnum.ADMIN);
        createUserIfNotExist("teacher", "teacher@gmail.com",  RoleEnum.TEACHER);
        createUserIfNotExist("staff", "staff@gmail.com",  RoleEnum.STAFF);

        log.info("Default admin, staff, teacher users initialization completed.");
    }

    private void createUserIfNotExist(String username, String email, RoleEnum role) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isEmpty()) {
            User newUser = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(username)) // It's a bad practice to use the username as a password, consider using a more secure password.
                    .email(email)
                    .role(role)
                    .build();
            userRepository.save(newUser);
            log.info("Created default {} user", username);
        } else {
            log.info("{} user already exists", username);
        }
    }
}