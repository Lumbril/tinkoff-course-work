package com.example.authserver.services.impl;

import com.example.authserver.dto.request.UserRegistrationRequest;
import com.example.authserver.entities.User;
import com.example.authserver.entities.enums.Role;
import com.example.authserver.repositories.UserRepository;
import com.example.authserver.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User create(UserRegistrationRequest userFromRequest) {
        User u = User.builder()
                .username(userFromRequest.getUsername())
                .password(bCryptPasswordEncoder.encode(userFromRequest.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        return userRepository.save(u);
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
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
