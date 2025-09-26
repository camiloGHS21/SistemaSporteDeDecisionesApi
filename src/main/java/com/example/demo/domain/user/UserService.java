package com.example.demo.domain.user;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User registerUser(User user) throws UserAlreadyExistsException;
}
