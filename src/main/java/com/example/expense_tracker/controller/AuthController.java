package com.example.expense_tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.expense_tracker.dto.LoginInput;
import com.example.expense_tracker.dto.RegisterInput;
import com.example.expense_tracker.service.JWTService;
import com.example.expense_tracker.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@RestController
public class AuthController {

    @Autowired
    UserService userService;

    @Autowired
    JWTService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterInput user) {
        userService.addUser(user);
        return new ResponseEntity<>("User logged in successfully", HttpStatus.OK);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginInput user) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()));

        String token = jwtService.generateToken(
                user.getUsername());

        return ResponseEntity.ok(token);
    }

    @GetMapping("/get-users")
    public ResponseEntity<?> getUsers() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }
}
