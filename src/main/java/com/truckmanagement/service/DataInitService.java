package com.truckmanagement.service;

import com.truckmanagement.model.*;
import com.truckmanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DataInitService implements CommandLineRunner {

    @Autowired private UserRepository userRepo;
    @Autowired private SettingsRepository settingsRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepo.existsByUsername("admin")) {
            User admin = new User(null, "admin",
                    passwordEncoder.encode("admin123"), "ADMIN");
            userRepo.save(admin);
            System.out.println("Default admin created: admin / admin123");
        }
        if (settingsRepo.count() == 0) {
            settingsRepo.save(new AppSettings(null, "GOODS CARRIER", "", "", "", ""));
        }
    }
}
