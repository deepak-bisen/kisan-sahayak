package com.kisan.user.controller;

import com.kisan.user.dto.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * REST Controller for User operations.
 */

public interface UserController {
    public ResponseEntity<UserDTO> register(UserDTO userDTO);
    public ResponseEntity<UserDTO> getUser(String userId);
    public ResponseEntity<UserDTO> getUserByPhone(String phoneNumber);
    public ResponseEntity<List<UserDTO>> getAllUsers();
}
