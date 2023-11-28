package com.example.resourceserver.services.impl;

import com.example.resourceserver.dto.TokenBody;
import com.example.resourceserver.entities.User;
import com.example.resourceserver.entities.enums.Role;
import com.example.resourceserver.repositories.UserRepository;
import com.example.resourceserver.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User createFromTokenBody(TokenBody tokenBody) {
        User user = User.builder()
                .authId(tokenBody.getUserId())
                .username(tokenBody.getUsername())
                .role(Role.ROLE_USER)
                .build();

        return userRepository.save(user);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    @Override
    public User getByAuthIdOrCreateFromTokenBody(TokenBody tokenBody) {
        Optional<User> user = userRepository.findByAuthId(tokenBody.getUserId());

        if (user.isPresent()) {
            return user.get();
        }

        return createFromTokenBody(tokenBody);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean userIsExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
