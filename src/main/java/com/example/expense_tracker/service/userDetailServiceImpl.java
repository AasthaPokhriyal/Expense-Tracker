package com.example.expense_tracker.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.expense_tracker.Entity.UserEntity;
import com.example.expense_tracker.Repository.UserRepo;

@Service
public class userDetailServiceImpl implements UserDetailsService {
    private final UserRepo userRepo;

    public userDetailServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userOpt = userRepo.findByUsername(username);
        UserEntity user = null;
        if (userOpt.isPresent()) {
            user = userOpt.get();
            if (user == null) {
                throw new UsernameNotFoundException("No such username found " + username);
            }
        }
        return org.springframework.security.core.userdetails.User.builder().username(user.getUsername())
                .password(user.getPassword()).roles("USER").build();
    }
}
