package com.kisan.user.controller;

import com.kisan.user.dto.LoginRequestDTO;
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
    public ResponseEntity<UserDTO> login(LoginRequestDTO loginRequest);
    public ResponseEntity<UserDTO> getUser(String userId);
    public ResponseEntity<UserDTO> getUserByPhone(String phoneNumber);
    public ResponseEntity<List<UserDTO>> getAllUsers();
    /**
     * Deletes a user from the system by their unique ID.
     * @param userId The UUID of the user to delete.
     */
    //    public ResponseEntity<Void> deleteUser(String phoneNumber);
    public ResponseEntity<Void> deleteUser(String userId);
    public ResponseEntity<UserDTO> updateUser(String userId,UserDTO userDTO);
}