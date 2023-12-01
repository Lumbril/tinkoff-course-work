package com.example.authserver.services.impl;

import com.example.authserver.dto.request.UserRegistrationRequest;
import com.example.authserver.entities.User;
import com.example.authserver.entities.enums.Role;
import com.example.authserver.exceptions.UserExistsException;
import com.example.authserver.exceptions.UserPasswordException;
import com.example.authserver.repositories.UserRepository;
import com.example.authserver.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public User create(UserRegistrationRequest userFromRequest) {
        if (!userFromRequest.getPassword().equals(userFromRequest.getPasswordConfirm())) {
            throw new UserPasswordException("Пароли не совпадают");
        }

        if (userIsExists(userFromRequest.getUsername())) {
            throw new UserExistsException();
        }

        User u = User.builder()
                .username(userFromRequest.getUsername())
                .password(bCryptPasswordEncoder.encode(userFromRequest.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        return userRepository.save(u);
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
