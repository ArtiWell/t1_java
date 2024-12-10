package ru.t1.java.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.t1.java.demo.dao.persistence.UserRepository;
import ru.t1.java.demo.entity.UserEntity;
import ru.t1.java.demo.exception.ConflictException;
import ru.t1.java.demo.model.reqest.UserRequest;
import ru.t1.java.demo.model.response.UserResponse;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder encoder;
    @InjectMocks
    private UserService userService;

    @Test
    void shouldLoadExistingUserByUsername() {
        UserEntity user = new UserEntity();
        user.setLogin("test_user");
        user.setPassword("test_password");

        when(userRepository.findByLogin("test_user")).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("test_user");

        assertNotNull(userDetails);
        assertEquals("test_user", userDetails.getUsername());
        assertEquals("test_password", userDetails.getPassword());
    }

    @Test
    void shouldReturnEmptyUserIfUsernameNotFound() {
        when(userRepository.findByLogin("non_existent_user")).thenReturn(Optional.empty());

        UserDetails userDetails = userService.loadUserByUsername("non_existent_user");

        assertNotNull(userDetails);
        assertNull(userDetails.getUsername());
        assertNull(userDetails.getPassword());
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        UserRequest userRequest = new UserRequest("new_user", "new_password");

        when(userRepository.findByLogin("new_user")).thenReturn(Optional.empty());
        when(encoder.encode("new_password")).thenReturn("encoded_password");

        UserResponse response = userService.registerUser(userRequest);

        assertNotNull(response);
        assertEquals("new_user", response.login());

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userCaptor.capture());

        UserEntity savedUser = userCaptor.getValue();
        assertEquals("new_user", savedUser.getLogin());
        assertEquals("encoded_password", savedUser.getPassword());
    }

    @Test
    void shouldThrowConflictExceptionIfUserAlreadyExists() {
        UserRequest userRequest = new UserRequest("existing_user", "password");
        when(userRepository.findByLogin("existing_user")).thenReturn(Optional.of(new UserEntity()));

        assertThrows(ConflictException.class, () -> userService.registerUser(userRequest));
    }


}