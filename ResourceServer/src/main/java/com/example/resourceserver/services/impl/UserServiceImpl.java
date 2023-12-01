package com.example.resourceserver.services.impl;

import com.example.resourceserver.dto.TokenBody;
import com.example.resourceserver.entities.User;
import com.example.resourceserver.entities.enums.Role;
import com.example.resourceserver.repositories.UserRepository;
import com.example.resourceserver.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User createFromTokenBody(TokenBody tokenBody) {
        User user = User.builder()
                .authId(tokenBody.getUserId())
                .username(tokenBody.getUsername())
                .role(Role.ROLE_USER)
                .build();

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public User getByAuthIdOrCreateFromTokenBody(TokenBody tokenBody) {
        Optional<User> user = userRepository.findByAuthId(tokenBody.getUserId());

        if (user.isPresent()) {
            return user.get();
        }

        return createFromTokenBody(tokenBody);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userIsExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
