package com.example.identityservice.configuration;

import com.example.identityservice.entity.Role;
import com.example.identityservice.entity.User;
import com.example.identityservice.repository.RoleRepository;
import com.example.identityservice.repository.UserRepository;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

  PasswordEncoder passwordEncoder;

  @Bean
  @ConditionalOnProperty(
      prefix = "spring",
      name = "datasource.driverClassName",
      havingValue = "com.mysql.cj.jdbc.Driver")
  ApplicationRunner applicationRunner(
      UserRepository userRepository, RoleRepository roleRepository) {
    return args -> {
      if (!userRepository.existsByUserName("admin")) {

        Role adminRole =
            roleRepository
                .findById("ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder().roleName("ADMIN").build()));

        User admin =
            User.builder()
                .userName("admin")
                .passWord(passwordEncoder.encode("admin"))
                .roles(Set.of(adminRole))
                .build();

        userRepository.save(admin);
        log.warn("Admin user created successfully by default password: admin please change it");
      }
    };
  }
}
