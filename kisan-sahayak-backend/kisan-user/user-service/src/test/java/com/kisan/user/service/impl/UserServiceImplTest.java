package com.kisan.user.service.impl;

import com.kisan.user.dto.UserDTO;
import com.kisan.user.repository.UserRepository;
import com.kisan.user.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUserShouldReturnConflictWhenPhoneNumberAlreadyExists() {
        UserDTO request = UserDTO.builder()
                .fullName("Test User")
                .phoneNumber("9876543210")
                .password("password123")
                .villageName("Village")
                .district("District")
                .state("State")
                .role("FARMER")
                .build();

        when(userRepository.existsByPhoneNumber("9876543210")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.registerUser(request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("User already registered with this number", exception.getReason());
    }
}
