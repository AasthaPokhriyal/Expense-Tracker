package com.example.expense_tracker.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.expense_tracker.Entity.UserEntity;
import com.example.expense_tracker.Repository.UserRepo;
import com.example.expense_tracker.dto.RegisterInput;

@Service
public class UserService {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    UserRepo userRepo;

    public void addUser(RegisterInput user) {
        UserEntity u = new UserEntity();
        u.setPassword(passwordEncoder.encode(user.getPassword()));
        u.setUsername(user.getUsername());
        Optional<UserEntity> userOpt = userRepo.findByUsername(user.getUsername());
        if (userOpt.isPresent())
            throw new IllegalArgumentException("Username already exists");
        userRepo.save(u);
    }

    public Optional<UserEntity> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public List<UserEntity> findAll() {
        return userRepo.findAll();
    }
}
