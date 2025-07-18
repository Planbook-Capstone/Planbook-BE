package com.BE.config;

import com.BE.enums.RoleEnum;
import com.BE.model.entity.AuthUser;
import com.BE.repository.AuthenRepository;
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
    AuthenRepository authenRepository;

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
        Optional<AuthUser> existingUser = authenRepository.findByUsername(username);
        if (existingUser.isEmpty()) {
            AuthUser newUser = new AuthUser();
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode(username)); // Cảnh báo: không nên dùng username làm password
            newUser.setEmail(email);
            newUser.setRole(role);
            authenRepository.save(newUser);
            log.info("Created default {} user", username);
        } else {
            log.info("{} user already exists", username);
        }
    }
}