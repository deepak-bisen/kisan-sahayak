package com.kisan.user.controller.impl;

import com.kisan.user.controller.UserController;
import com.kisan.user.dto.AuthResponseDTO;
import com.kisan.user.dto.LoginRequestDTO;
import com.kisan.user.dto.UserDTO;
import com.kisan.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public ResponseEntity<UserDTO> registerUser(UserDTO userDTO) {
        return new ResponseEntity<>(userService.registerUser(userDTO), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AuthResponseDTO> loginUser(LoginRequestDTO loginRequest) {
        // Now returns AuthResponseDTO which includes the JWT token
        return ResponseEntity.ok(userService.loginUser(loginRequest));
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
        userService.getUserByPhoneNumber(phoneNumber);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UserDTO> updateUser(String userId, UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(userId, userDTO));
    }
}
