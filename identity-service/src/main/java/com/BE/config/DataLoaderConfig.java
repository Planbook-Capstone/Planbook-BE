package com.BE.config;

import com.BE.enums.RoleEnum;
import com.BE.enums.StatusEnum;
import com.BE.model.entity.User;
import com.BE.repository.AuthenRepository;
import com.BE.service.interfaceServices.IWalletService;
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
    IWalletService iWalletService;

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
        createUserIfNotExist("partner", "partner@gmail.com",  RoleEnum.PARTNER);

        log.info("Default admin, staff, teacher users initialization completed.");
    }

    private void createUserIfNotExist(String username, String email, RoleEnum role) {
        Optional<User> existingUser = authenRepository.findByUsername(username);
        if (existingUser.isEmpty()) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode(username)); // Cảnh báo: không nên dùng username làm password
            newUser.setEmail(email);
            newUser.setRole(role);
            newUser.setStatus(StatusEnum.ACTIVE);
            if(RoleEnum.TEACHER.equals(role) || RoleEnum.PARTNER.equals(role)){
                iWalletService.create(newUser);
            }
            authenRepository.save(newUser);
            log.info("Created default {} user", username);
        } else {
            log.info("{} user already exists", username);
        }
    }
}