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
        log.info("Checking and initializing default roles and users...");

        User adminUser = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .email("admin@gmail.com")
                .fullName("admin")
                .role(RoleEnum.ADMIN)
                .build();

        User teacherUser = User.builder()
                .username("teacher")
                .password(passwordEncoder.encode("teacher"))
                .email("teacher@gmail.com")
                .fullName("teacher")
                .role(RoleEnum.TEACHER)
                .build();

        User staffUser = User.builder()
                .username("staff")
                .password(passwordEncoder.encode("staff"))
                .email("staff@gmail.com")
                .fullName("staff")
                .role(RoleEnum.STAFF)
                .build();

        userRepository.save(adminUser);
        userRepository.save(teacherUser);
        userRepository.save(staffUser);

        log.info("Created default admin, staff, teacher User");
    }


}
