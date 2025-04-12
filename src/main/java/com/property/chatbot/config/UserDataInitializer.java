package com.property.chatbot.config;

import com.property.chatbot.entities.user.User;
import com.property.chatbot.repositories.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class UserDataInitializer {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserDataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            User user = User.builder().username("user").password(passwordEncoder.encode("password")).roles(Set.of("USER")).build();
            userRepository.save(user);
        };
    }
}
