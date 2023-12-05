package com.example.resourceserver.services;


import com.example.resourceserver.dto.TokenBody;
import com.example.resourceserver.entities.User;
import com.example.resourceserver.entities.enums.Role;
import com.example.resourceserver.repositories.UserRepository;
import com.example.resourceserver.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User tempUser = new User(1L, 1L,
            "string", null,
            null, Role.ROLE_USER);

    private TokenBody tempTokenBody = new TokenBody(1L, "string",
            "access", null,
            null, null);

    @Test
    public void createFromTokenBodyTest() {
        Mockito.doReturn(tempUser).when(userRepository).save(Mockito.any(User.class));

        User user = userService.createFromTokenBody(tempTokenBody);

        assertEquals(tempUser, user);
    }

    @Test
    public void getByIdTest() {
        Mockito.doReturn(Optional.of(tempUser)).when(userRepository).findById(1L);

        User user = userService.getById(1L);

        assertEquals(tempUser, user);
    }

    @Test
    public void getByUsernameTest() {
        Mockito.doReturn(Optional.of(tempUser)).when(userRepository).findByUsername(Mockito.anyString());

        User user = userService.getByUsername("string");

        assertEquals(tempUser, user);
    }

    @Test
    public void getByAuthIdTest() {
        Mockito.doReturn(Optional.of(tempUser)).when(userRepository).findByAuthId(Mockito.anyLong());

        User user = userService.getByAuthIdOrCreateFromTokenBody(tempTokenBody);

        assertEquals(tempUser, user);
    }

    @Test
    public void createFromTokenBodyAndGetByAuthIdTest() {
        Mockito.doReturn(tempUser).when(userRepository).save(Mockito.any(User.class));
        Mockito.doReturn(Optional.empty()).when(userRepository).findByAuthId(Mockito.anyLong());

        User user = userService.getByAuthIdOrCreateFromTokenBody(tempTokenBody);

        assertEquals(tempUser, user);
    }

    @Test
    public void getAllTest() {
        Mockito.doReturn(List.of(tempUser)).when(userRepository).findAll();

        List<User> users = userService.getAll();

        assertIterableEquals(List.of(tempUser), users);
    }

    @Test
    public void deleteByIdTest() {
        Mockito.doNothing().when(userRepository).deleteById(anyLong());

        userService.deleteById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void userIsExistsTest() {
        Mockito.doReturn(Optional.of(tempUser)).when(userRepository).findByUsername(anyString());

        boolean isExists = userService.userIsExists(anyString());

        assertEquals(true, isExists);
    }
}
