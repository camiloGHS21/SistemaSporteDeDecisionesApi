package com.example.demo.application;

import io.jsonwebtoken.ExpiredJwtException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.application.Auth.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", "dGhpcy1pcy1hLXN1cGVyLXNlY3JldC1rZXktZm9yLWp3dC10b2tlbi1nZW5lcmF0aW9u");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", 86400000L);
        userDetails = new User("test@test.com", "password", new ArrayList<>());
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        String token = jwtTokenProvider.generateToken(userDetails);
        assertNotNull(token);
        assertEquals(userDetails.getUsername(), jwtTokenProvider.extractUsername(token));
    }

    @Test
    void isTokenValid_shouldReturnTrue_forValidToken() {
        String token = jwtTokenProvider.generateToken(userDetails);
        assertTrue(jwtTokenProvider.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_shouldReturnFalse_forInvalidToken() {
        String token = jwtTokenProvider.generateToken(userDetails);
        UserDetails otherUserDetails = new User("other@test.com", "password", new ArrayList<>());
        assertFalse(jwtTokenProvider.isTokenValid(token, otherUserDetails));
    }

    @Test
    void isTokenValid_shouldThrowExpiredJwtException_forExpiredToken() {
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", -1L);
        String token = jwtTokenProvider.generateToken(userDetails);
        assertThrows(ExpiredJwtException.class, () -> jwtTokenProvider.isTokenValid(token, userDetails));
    }
}
