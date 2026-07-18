package com.kisan.user.service.impl;

import com.kisan.user.dto.AuthResponseDTO;
import com.kisan.user.dto.LoginRequestDTO;
import com.kisan.user.dto.UserDTO;
import com.kisan.user.entity.User;
import com.kisan.user.repository.UserRepository;
import com.kisan.user.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
                .roles(List.of("FARMER"))
                .build();

        when(userRepository.existsByPhoneNumber("9876543210")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.registerUser(request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("User already registered with this number", exception.getReason());
    }

    @Test
    void registerUserShouldDefaultRoleWhenNotProvided() {
        UserDTO request = UserDTO.builder()
                .fullName("Test User")
                .phoneNumber("9876543210")
                .password("password123")
                .villageName("Village")
                .district("District")
                .state("State")
                .build();

        when(userRepository.existsByPhoneNumber("9876543210")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO response = userService.registerUser(request);

        assertEquals(List.of("FARMER"), response.getRoles());
    }

    @Test
    void loginUserShouldGenerateTokenForValidCredentials() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setPhoneNumber("9876543210");
        request.setPassword("password123");

        User user = User.builder()
                .id("user-1")
                .name("Test User")
                .phoneNumber("9876543210")
                .password("hashed-password")
                .villageName("Village")
                .district("District")
                .state("State")
                .roles("FARMER")
                .build();

        when(userRepository.findByPhoneNumber("9876543210")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed-password")).thenReturn(true);
        ReflectionTestUtils.setField(jwtUtil, "secret", "c2VjcmV0LWtleS1mb3ItdGVzdGluZy1rZXktMTIzNDU2Nzg5MDEyMw==");
        when(jwtUtil.generateToken("9876543210", "user-1", "FARMER")).thenReturn("jwt-token");

        AuthResponseDTO response = userService.loginUser(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("user-1", response.getUser().getUserId());
    }
}
