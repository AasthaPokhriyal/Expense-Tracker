package com.example.expense_tracker.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

@Service
public class JWTService {

        private static final String SECRET_KEY = "mySuperSecretKeyMySuperSecretKey123456";

        public String generateToken(String username) {

                return Jwts.builder().subject(username).issuedAt(new Date())
                                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())).compact();
        }

        private Key getSigningKey() {
                return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        }

        public String extractUsername(String token) {

                return Jwts.parser().verifyWith((SecretKey) getSigningKey()).build()
                                .parseSignedClaims(token).getPayload().getSubject();
        }

        public boolean validateToken(String token, UserDetails userDetails) {

                String username = extractUsername(token);

                return username.equals(userDetails.getUsername());
        }
}