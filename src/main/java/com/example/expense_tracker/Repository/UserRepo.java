package com.example.expense_tracker.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.expense_tracker.Entity.UserEntity;

public interface UserRepo extends JpaRepository<UserEntity, String> {
    public UserEntity save(UserEntity user);

    public Optional<UserEntity> findByUsername(String username);
}
