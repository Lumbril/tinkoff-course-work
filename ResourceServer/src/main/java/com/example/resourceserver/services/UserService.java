package com.example.resourceserver.services;

import com.example.resourceserver.dto.TokenBody;
import com.example.resourceserver.entities.User;

import java.util.List;

public interface UserService {
    User createFromTokenBody(TokenBody tokenBody);
    User getById(Long id);
    User getByUsername(String username);
    User getByAuthIdOrCreateFromTokenBody(TokenBody tokenBody);
    List<User> getAll();
    void deleteById(Long id);
    boolean userIsExists(String username);
}
