package com.kisan.user.controller.impl;

import com.kisan.user.controller.UserController;
import com.kisan.user.dto.AuthResponseDTO;
import com.kisan.user.dto.LoginRequestDTO;
import com.kisan.user.dto.UserDTO;
import com.kisan.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public ResponseEntity<UserDTO> registerUser(UserDTO userDTO) {
        log.info("POST /api/users/register received for phoneNumber={}", userDTO != null ? userDTO.getPhoneNumber() : "null");
        UserDTO response = userService.registerUser(userDTO);
        log.info("POST /api/users/register completed for userId={}", response.getUserId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<AuthResponseDTO> loginUser(LoginRequestDTO loginRequest) {
        log.info("POST /api/users/login received for phoneNumber={}", loginRequest != null ? loginRequest.getPhoneNumber() : "null");
        AuthResponseDTO response = userService.loginUser(loginRequest);
        log.info("POST /api/users/login completed for userId={}", response.getUser() != null ? response.getUser().getUserId() : "unknown");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UserDTO> getUser(String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Override
    public ResponseEntity<UserDTO> getUserByPhone(String phoneNumber) {
        return ResponseEntity.ok(userService.getUserByPhoneNumber(phoneNumber));
    }

    @Override
    public ResponseEntity<String> refreshToken(String token) {
        log.info("POST /api/users/refresh received");
        String newToken = userService.refreshToken(token);
        return ResponseEntity.ok(newToken);
    }

    @Override
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Override
    public ResponseEntity<Void> deleteUser(String userId) {
        userService.deleteUserByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteUserByPhone(String phoneNumber) {
        log.info("DELETE /api/users/phone/{} received", phoneNumber);
        userService.deleteUserByPhone(phoneNumber);
        log.info("DELETE /api/users/phone/{} completed", phoneNumber);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UserDTO> updateUser(String userId, UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(userId, userDTO));
    }
}
