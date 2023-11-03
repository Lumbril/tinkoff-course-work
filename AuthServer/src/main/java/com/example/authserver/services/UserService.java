package com.example.authserver.services;

import com.example.authserver.dto.request.UserRegistrationRequest;
import com.example.authserver.entities.User;

public interface UserService {
    User create(UserRegistrationRequest userFromRequest);
    User getById(Long id);
    User getByUsername(String username);
    void deleteById(Long id);
}
